package com.example.gitsync;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.branch.GitBranchesCollection;
import git4idea.commands.Git;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GitBranchSync extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
        // System.out.println("Things to do before action");
        // TODO Make sure git is installed - disable if not
        // TODO Make sure project directory is a git repo - disable if not
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Get Current project setup...
        Project currentProject = event.getProject();
        VirtualFile baseDir = currentProject.getBaseDir();
        if(baseDir == null) return;
        int LIMIT=2;
        String basePath = baseDir.getPath();
        FileEditorManager manager = FileEditorManager.getInstance(currentProject);

        // Git Things...
        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(currentProject);
        List<GitRepository> repositories = gitRepositoryManager.getRepositories();
        GitRepository currentRepo;
        String currentBranchName;

        if(repositories.size() == 1){
            currentRepo = repositories.get(0);
            currentBranchName = currentRepo.getCurrentBranch().getName();
            System.out.println("Current Branches: " + currentRepo.getBranches());
            System.out.println("Current current branch name: " + currentBranchName);
        } else return;

        GitBranchesCollection localBranches = currentRepo.getBranches();
//        if(localBranches.findLocalBranch("main") != null){
//            System.out.println("THERE IS A MAIN");
//        }
        try {
            // Get Latest Git diff
            final Collection<String> latestDiff = GitUtil.getPathsDiffBetweenRefs(
                    Git.getInstance(),
                    currentRepo,
                    currentBranchName,
                    "main" // TODO change to default branch
            );

            // Change into set
            Set<String> latestDiffSet = new HashSet<>(latestDiff);
            List<String> latestDiffList = new ArrayList<>(latestDiffSet);

            // Close currently open saved files
            System.out.println("Closing currently open files...");
            VirtualFile currentOpenFiles[] = manager.getOpenFiles();
            for(VirtualFile currentOpenFile: currentOpenFiles){
                manager.closeFile(currentOpenFile);
            }

            // Open Files
            System.out.println("Opening Files...");
            int relativePathStartIndex = basePath.length() + 1;
            VirtualFile vFile;

            for(String fileAbsolutePath: latestDiffList.subList(0, LIMIT)){
                String tempFileName = "";

                if(fileAbsolutePath.contains(baseDir.getName() + "/")){
                    System.out.println("HAS /: " + fileAbsolutePath);
                    tempFileName = fileAbsolutePath.substring(relativePathStartIndex);
                }
                vFile = baseDir.findFileByRelativePath(tempFileName);

                if(vFile != null){
                    new OpenFileDescriptor(currentProject, vFile).navigate(true);
                }
            }
        } catch (VcsException e) {
            System.out.println("THERE IS AN ERROR, WELL FIX IT");
            throw new RuntimeException(e);
        }
    }
}