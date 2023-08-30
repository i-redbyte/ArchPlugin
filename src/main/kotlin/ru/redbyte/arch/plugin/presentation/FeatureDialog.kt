package ru.redbyte.arch.plugin.presentation

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
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

        featureNameHint.alignmentX = Component.LEFT_ALIGNMENT
        featureNameDescription.alignmentX = Component.LEFT_ALIGNMENT
        featureNameField.alignmentX = Component.LEFT_ALIGNMENT
        createDiCheckBox.alignmentX = Component.LEFT_ALIGNMENT
        createFragment.alignmentX = Component.LEFT_ALIGNMENT
        typeList.alignmentX = Component.LEFT_ALIGNMENT

        dialogPanel.add(featureNameHint)
        dialogPanel.add(featureNameField)
        dialogPanel.add(featureNameDescription)
        dialogPanel.add(Box.createRigidArea(Dimension(0, 10)))
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
        featurePresenter.createFeature(
            FeatureParams(
                featureNameField.text,
                createDiCheckBox.isSelected,
                createFragment.isSelected
            )
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
}