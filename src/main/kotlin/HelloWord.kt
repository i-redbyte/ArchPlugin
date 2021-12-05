import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class HelloWord : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { show(it, "Hello plugin") }
    }

}

private fun show(project: Project, message: String) {
    NotificationGroupManager.getInstance().getNotificationGroup("RedByte Notification")
        .createNotification(message, NotificationType.ERROR)
        .notify(project);
}
