package ru.redbyte.arch.plugin.ui

import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorTextField
import ru.redbyte.arch.plugin.data.generation.FeatureCreator
import ru.redbyte.arch.plugin.data.generation.FeatureParams
import ru.redbyte.arch.plugin.data.utils.getPackageName
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

    private val presenter: FeaturePresenter = FeaturePresenterImpl(this, FeatureCreator(project))

    private val notificationGroup = NotificationGroup("Arch plugin errors", NotificationDisplayType.BALLOON, true)

    private val featureNameField = EditorTextField().apply {
        setOneLineMode(true)
        setPreferredWidth(350)
    }
    private val defaultPackageName: String by lazy {
        getPackageName(project)
    }
    private val createDiCheckBox = JCheckBox("Create DI components").apply {
        isSelected = true
    }
    private val createFragment = JCheckBox("Create fragment").apply {
        isSelected = true
    }

    private val useCustomPackageCheckBox = JCheckBox("Use custom package name").apply {
        isSelected = false
    }

    private val customPackageNameField = EditorTextField().apply {
        setOneLineMode(true)
        setPreferredWidth(350)
        isEnabled = false
        text = defaultPackageName
    }

    private val directoriesComboBox = ComboBox(getTopLevelDirectories().toTypedArray())

    private val typeList = ComboBox(presenter.getTypeArray())

    init {
        init()
        title = "Create new feature"
        useCustomPackageCheckBox.addActionListener {
            customPackageNameField.isEnabled = useCustomPackageCheckBox.isSelected
            if (!useCustomPackageCheckBox.isSelected) {
                customPackageNameField.text = defaultPackageName
            }
        }
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
        useCustomPackageCheckBox.alignmentX = Component.LEFT_ALIGNMENT
        customPackageNameField.alignmentX = Component.LEFT_ALIGNMENT
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

        dialogPanel.add(useCustomPackageCheckBox)
        dialogPanel.add(customPackageNameField)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(createDiCheckBox)
        dialogPanel.add(createFragment)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        typeList.addActionListener {
            presenter.onTypeSelected(typeList.selectedItem as String)
        }

        return dialogPanel
    }

    override fun doOKAction() {
        val selectedDirectory = directoriesComboBox.selectedItem as String
        val customPackageName = if (useCustomPackageCheckBox.isSelected) {
            customPackageNameField.text
        } else {
            defaultPackageName
        }

        presenter.createFeature(
            FeatureParams(
                featureNameField.text,
                createDiCheckBox.isSelected,
                createFragment.isSelected,
                selectedDirectory,
                customPackageName
            ),
        )
    }

    override fun doValidate(): ValidationInfo? {
        return presenter.validate(featureNameField.text)
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

    private fun isSystemDirectory(name: String): Boolean {
        val systemDirs = listOf(".idea", "build", "gradle", "out")
        return name in systemDirs || name.startsWith(".")
    }
}
