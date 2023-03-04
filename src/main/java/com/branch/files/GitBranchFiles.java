package com.branch.files;

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

public class GitBranchFiles extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Make sure project directory is a git repo - disable if not
        Project currentProject = event.getProject();
        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(currentProject);
        List<GitRepository> repositories = gitRepositoryManager.getRepositories();

        if(repositories.size() == 0){
            System.out.println("No Git repo found, plugin will be disabled");
            event.getPresentation().setEnabled(false);
        } else {
            System.out.println("Git repo found, plugin will be enabled");
            event.getPresentation().setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Get Current project setup...
        Project currentProject = event.getProject();
        assert currentProject != null;
        VirtualFile baseDir = currentProject.getBaseDir();
        if(baseDir == null) return;
        int LIMIT=6; //TODO can make this input based
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

            List<VirtualFile> latestDiffList = new ArrayList<>();
            Set<String> latestDiffSet = new HashSet<>();
            VirtualFile tempFile;

            for(String pathDiff: latestDiff) {
                if(!latestDiffSet.contains(pathDiff)){
                    tempFile = findFile(pathDiff, baseDir);
                    if(tempFile != null){
                        latestDiffSet.add(pathDiff);
                        latestDiffList.add(tempFile);
                        if(latestDiffSet.size() == LIMIT){
                            break;
                        }
                    }
                }
            }

            // Close currently open saved files
            System.out.println("Closing currently open files...");
            VirtualFile currentOpenFiles[] = manager.getOpenFiles();
            for(VirtualFile currentOpenFile: currentOpenFiles){
                manager.closeFile(currentOpenFile);
            }

            // Open Files
            System.out.println("Opening Files...");
            for(VirtualFile file: latestDiffList){
                new OpenFileDescriptor(currentProject, file).navigate(true);
            }
        } catch (VcsException e) {
            System.out.println("THERE IS AN ERROR, WELL FIX IT");
            throw new RuntimeException(e);
        }
    }

    private VirtualFile findFile(String fileAbsolutePath, VirtualFile baseDir) {
        String tempFileName = "";
        String basePath = baseDir.getPath();
        int relativePathStartIndex = basePath.length() + 1;
        if(fileAbsolutePath.contains(baseDir.getName() + "/")){
            tempFileName = fileAbsolutePath.substring(relativePathStartIndex);
        }
        return baseDir.findFileByRelativePath(tempFileName);
    }
}