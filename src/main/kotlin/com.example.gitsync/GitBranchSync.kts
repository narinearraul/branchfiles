//import com.intellij.openapi.actionSystem.AnAction
//import com.intellij.openapi.actionSystem.AnActionEvent
//import com.intellij.openapi.actionSystem.ActionUpdateThread
//
//class GitBranchSync : AnAction() {
//    override fun update(@NotNull event: AnActionEvent) {
//        // Using the event, evaluate the context,
//        // and enable or disable the action.
//        logger.info("IN UPDATE!!!")
//        val currentProject: Project = event.getProject()
//        println("WHAT IS CURRENT PROJECT: ?", currentProject)
//        event.getPresentation().setEnabledAndVisible(currentProject != null)
//    }
//
//    override fun actionPerformed(@NotNull event: AnActionEvent) {
//        // Using the event, implement an action.
//        logger.info("SOMETHING HERE")
//        val currentProject: Project = event.getProject()
//        println("WHAT IS THIS? ", currentProject)
//    }
//
//    @NotNull
//    fun getActionUpdateThread(): ActionUpdateThread? {
//        print("WHAT IS THIS")
//        return ActionUpdateThread.BGT
//    }}
//}