package ru.redbyte.arch.plugin.presentation

import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorTextField
import ru.redbyte.arch.plugin.data.FeatureCreator
import ru.redbyte.arch.plugin.data.FeatureParams
import java.awt.Component
import java.awt.Dimension
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
            getPackageName()
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

    private fun getPackageName(): String {
        val projectBaseDir = LocalFileSystem
            .getInstance()
            .findFileByPath(
                project.basePath ?: return ""
            )

        val psiProjectBaseDir = projectBaseDir
            ?.let {
                PsiManager
                    .getInstance(project)
                    .findDirectory(it)
            } ?: return ""

        val srcMainJavaDir = psiProjectBaseDir.subdirectories
            .firstOrNull { dir -> dir.name == "src" }
            ?.subdirectories
            ?.firstOrNull { dir -> dir.name == "main" }
            ?.subdirectories
            ?.firstOrNull { dir -> dir.name == "java" }
            ?: throw IllegalArgumentException("[FeatureDialog] srcMainJavaDir is null")
        showNotification(project, "getPackageName", srcMainJavaDir.toString())
        return getPackageNameFromDir(srcMainJavaDir)
    }

    private fun getPackageNameFromDir(dir: PsiDirectory): String {
        val packageNames = mutableListOf<String>()

        var currentDir: PsiDirectory? = dir
        while (currentDir != null && currentDir.name != "java") {
            packageNames.add(currentDir.name)
            currentDir = currentDir.parent
        }

        val joinToString = packageNames.reversed().joinToString(".")
        showNotification(project, "getPackageNameFromDir", joinToString)
        return joinToString
    }

    fun showNotification(project: Project, title: String, content: String) {
        val notificationGroup = NotificationGroup.findRegisteredGroup("ru.redbyte.arch.notifications")
            ?: NotificationGroup("ru.redbyte.arch.notifications", NotificationDisplayType.BALLOON, true)

        val notification = notificationGroup.createNotification(
            content, NotificationType.INFORMATION
        )
        Notifications.Bus.notify(notification, project)
    }

    private fun isSystemDirectory(name: String): Boolean {
        val systemDirs = listOf(".idea", "build", "gradle", "out")
        return name in systemDirs || name.startsWith(".")
    }
}
