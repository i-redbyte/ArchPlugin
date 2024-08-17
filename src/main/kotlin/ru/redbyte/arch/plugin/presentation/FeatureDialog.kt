package ru.redbyte.arch.plugin.presentation

import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorTextField
import ru.redbyte.arch.plugin.data.FeatureCreator
import ru.redbyte.arch.plugin.data.FeatureParams
import ru.redbyte.arch.plugin.showMessage
import java.awt.Component
import java.awt.Dimension
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


interface FeatureView {

    fun closeSuccessfully()

    fun closeWithError(message: String)

    fun enableCheckBoxes(enable: Boolean)
}

class FeatureDialog(private val project: Project) : DialogWrapper(true), FeatureView {

    private val featurePresenter: FeaturePresenter = FeaturePresenterImpl(this, FeatureCreator(project))

    private val notificationGroup = NotificationGroup("Arch plugin errors", NotificationDisplayType.BALLOON, true)

    private val featureNameField = EditorTextField().apply {
        setOneLineMode(true)
        setPreferredWidth(350)
    }

    private val createDiCheckBox = JCheckBox("Create DI components").apply {
        isSelected = true
    }
    private val createFragment = JCheckBox("Create fragment").apply {
        isSelected = true
    }

    private val directoriesComboBox = ComboBox(getTopLevelDirectories().toTypedArray())

    private val typeList = ComboBox(featurePresenter.getTypeArray())

    init {
        init()
        title = "Create new feature"
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel()
        val layout = BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS)

        dialogPanel.layout = layout
        val featureNameHint = JLabel("Feature name")
        val featureNameDescription = JLabel("Example: super-puper")
        val directoryHint = JLabel("Select directory")

        featureNameHint.alignmentX = Component.LEFT_ALIGNMENT
        featureNameDescription.alignmentX = Component.LEFT_ALIGNMENT
        featureNameField.alignmentX = Component.LEFT_ALIGNMENT
        createDiCheckBox.alignmentX = Component.LEFT_ALIGNMENT
        createFragment.alignmentX = Component.LEFT_ALIGNMENT
        typeList.alignmentX = Component.LEFT_ALIGNMENT
        directoriesComboBox.alignmentX = Component.LEFT_ALIGNMENT
        directoryHint.alignmentX = Component.LEFT_ALIGNMENT

        dialogPanel.add(directoryHint)
        dialogPanel.add(directoriesComboBox)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(featureNameHint)
        dialogPanel.add(featureNameField)
        dialogPanel.add(featureNameDescription)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(typeList)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(createDiCheckBox)
        dialogPanel.add(createFragment)

        typeList.addActionListener {
            featurePresenter.onTypeSelected(typeList.selectedItem as String)
        }

        return dialogPanel
    }

    override fun doOKAction() {
        val selectedDirectory = directoriesComboBox.selectedItem as String
        featurePresenter.createFeature(
            FeatureParams(
                featureNameField.text,
                createDiCheckBox.isSelected,
                createFragment.isSelected,
                selectedDirectory
            ),
            getTopLevelPackageName(project)
            //getPackageName(project)
        )
    }

    override fun doValidate(): ValidationInfo? {
        return featurePresenter.validate(featureNameField.text)
    }

    override fun closeSuccessfully() {
        close(OK_EXIT_CODE)
    }

    override fun closeWithError(message: String) {
        notificationGroup.createNotification(message, NotificationType.ERROR).notify(project)
        close(CANCEL_EXIT_CODE)
    }

    override fun enableCheckBoxes(enable: Boolean) {
        createDiCheckBox.isEnabled = enable
        createFragment.isEnabled = enable
    }


    fun getPackageName(project: Project): String {
        val projectBasePath = project.basePath
            ?: throw IllegalArgumentException("[getPackageName] project.basePath is null")

        val projectBaseDir = LocalFileSystem.getInstance().findFileByPath(projectBasePath)
            ?: throw IllegalStateException("[getPackageName] Unable to find project base directory at $projectBasePath")

        val buildFilePaths = listOf(
            "${projectBaseDir.path}/app/build.gradle.kts",
            "${projectBaseDir.path}/app/build.gradle"
        )

        val buildFile = buildFilePaths.map(::File).firstOrNull { it.exists() }
            ?: throw IllegalStateException("[getPackageName] No build.gradle or build.gradle.kts found in the project")

        val buildFileContent = buildFile.readText()

        val namespaceRegex = Regex("""namespace\s*=\s*["'](.+?)["']""")
        val matchResult = namespaceRegex.find(buildFileContent)

        return matchResult?.groups?.get(1)?.value
            ?: throw IllegalStateException("[getPackageName] Namespace not found in ${buildFile.name}")
    }
    fun getTopLevelPackageName(project: Project): String {
        // Получение базовой директории проекта
        val projectBasePath = project.basePath
            ?: throw IllegalArgumentException("[getTopLevelPackageName] project.basePath is null")

        val projectBaseDir = LocalFileSystem.getInstance().findFileByPath(projectBasePath)
            ?: throw IllegalStateException("[getTopLevelPackageName] Unable to find project base directory at $projectBasePath")

        // Пути к исходным кодам Java и Kotlin
        val sourceDirectories = listOf(
            "${projectBaseDir.path}/app/src/main/java",
            "${projectBaseDir.path}/app/src/main/kotlin"
        )

        // Поиск первого файла с кодом в указанных директориях
        val packageName = sourceDirectories.asSequence()
            .map(::File)
            .filter { it.exists() }
            .mapNotNull { findTopLevelPackageName(it) }
            .firstOrNull()

        return packageName ?: throw IllegalStateException("[getTopLevelPackageName] No package name found in source directories")
    }

    // Рекурсивный поиск верхнеуровневого имени пакета
    fun findTopLevelPackageName(directory: File): String? {
        directory.walkTopDown().forEach { file ->
            if (file.isFile && (file.extension == "java" || file.extension == "kt")) {
                val packageName = extractPackageNameFromFile(file)
                if (packageName != null) {
                    return packageName
                }
            }
        }
        return null
    }

    fun extractPackageNameFromFile(file: File): String? {
        val packageRegex = Regex("""^\s*package\s+([\w.]+)""")
        file.useLines { lines ->
            for (line in lines) {
                val matchResult = packageRegex.find(line)
                if (matchResult != null) {
                    return matchResult.groups[1]?.value
                }
            }
        }
        return null
    }

    private fun getTopLevelDirectories(): List<String> {
        val projectBaseDir = LocalFileSystem
            .getInstance()
            .findFileByPath(
                project.basePath ?: return emptyList()
            )
        val psiProjectBaseDir = projectBaseDir
            ?.let {
                PsiManager
                    .getInstance(project)
                    .findDirectory(it)
            } ?: return emptyList()

        return psiProjectBaseDir.subdirectories
            .filter { dir -> !isSystemDirectory(dir.name) }
            .map { it.name }
    }

    private fun isSystemDirectory(name: String): Boolean {
        val systemDirs = listOf(".idea", "build", "gradle", "out")
        return name in systemDirs || name.startsWith(".")
    }
}
