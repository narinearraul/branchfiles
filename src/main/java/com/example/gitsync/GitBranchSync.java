package com.example.gitsync;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class GitBranchSync extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
        System.out.println("HERE I AM UPDATE");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Get Current project
        Project currentProject = event.getProject();

        // TODO: connect to GIT
        System.out.println("Opening Files...");
        VirtualFile baseDir = currentProject.getBaseDir();
        if(baseDir == null)  {return;}
        VirtualFile filePath = baseDir.findFileByRelativePath("/MinHash.java");
        new OpenFileDescriptor(currentProject, filePath).navigate(true);
        System.out.println("HERE I AM, AFTER ");
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}