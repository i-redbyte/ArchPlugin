package ru.redbyte.arch.plugin.ui

import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.EditorTextField
import ru.redbyte.arch.plugin.generation.*
import ru.redbyte.arch.plugin.generation.models.FeatureContract
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.generation.models.FeatureParams
import ru.redbyte.arch.plugin.utils.getPackageName
import ru.redbyte.arch.plugin.utils.loadTopLevelDirectories
import java.awt.Component
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.BoxLayout.X_AXIS
import javax.swing.BoxLayout.Y_AXIS

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
    private val defaultPackageName: String by lazy { getPackageName(project) }
    private val topLevelDirectories: List<String> by lazy { loadTopLevelDirectories(project) }

    private val createDiCheckBox = JCheckBox("Create DI components").apply {
        isSelected = false
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

    private val directoriesComboBox = ComboBox(topLevelDirectories.toTypedArray())

    private val withStateCheckBox = JCheckBox("With State").apply {
        isSelected = true
    }
    private val withActionsCheckBox = JCheckBox("With Actions").apply {
        isSelected = true
    }
    private val withEffectCheckBox = JCheckBox("With Effect").apply {
        isSelected = true
    }

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
        var layout = BoxLayout(dialogPanel, Y_AXIS)

        dialogPanel.layout = layout
        val featureNameHint = JLabel("Feature name")
        val featureNameDescription = JLabel("Example: super-puper")
        val directoryHint = JLabel("Select directory")

        featureNameHint.alignmentX = Component.LEFT_ALIGNMENT
        featureNameDescription.alignmentX = Component.LEFT_ALIGNMENT
        featureNameField.alignmentX = Component.LEFT_ALIGNMENT
        createDiCheckBox.alignmentX = Component.LEFT_ALIGNMENT
        useCustomPackageCheckBox.alignmentX = Component.LEFT_ALIGNMENT
        customPackageNameField.alignmentX = Component.LEFT_ALIGNMENT
        directoriesComboBox.alignmentX = Component.LEFT_ALIGNMENT
        directoryHint.alignmentX = Component.LEFT_ALIGNMENT

        dialogPanel.add(directoryHint)
        dialogPanel.add(directoriesComboBox)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(featureNameHint)
        dialogPanel.add(featureNameField)
        dialogPanel.add(featureNameDescription)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(useCustomPackageCheckBox)
        dialogPanel.add(customPackageNameField)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        val checkBoxPanel = JPanel().apply {
            layout = BoxLayout(this, X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            add(withStateCheckBox)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(withActionsCheckBox)
            add(Box.createRigidArea(Dimension(10, 0)))
            add(withEffectCheckBox)
        }
        dialogPanel.add(checkBoxPanel)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        dialogPanel.add(createDiCheckBox)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))

        return dialogPanel
    }

    override fun doOKAction() {
        val selectedDirectory = directoriesComboBox.selectedItem as String
        val packageName = if (useCustomPackageCheckBox.isSelected) {
            customPackageNameField.text
        } else {
            defaultPackageName
        }

        presenter.createFeature(
            FeatureParams(
                metadata = FeatureMetadata(
                    featureName = featureNameField.text,
                    selectedDirectory = selectedDirectory,
                    packageName = packageName,
                ),
                contractParam = FeatureContract(
                    withState = withStateCheckBox.isSelected,
                    withActions = withActionsCheckBox.isSelected,
                    withEffect = withEffectCheckBox.isSelected
                ),
                withDIFiles = createDiCheckBox.isSelected,
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
        withStateCheckBox.isEnabled = enable
        withActionsCheckBox.isEnabled = enable
        withEffectCheckBox.isEnabled = enable
    }

}
