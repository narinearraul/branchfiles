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

        if(repositories.size() != 1){
            System.out.println("BranchFiles: No Git repo found, plugin will be disabled");
            event.getPresentation().setEnabled(false);
        } else {
            GitRepository currentRepo;
            String currentBranchName;
            System.out.println("BranchFiles: Git repo found, checking branch");
            currentRepo = repositories.get(0);
            currentBranchName = currentRepo.getCurrentBranch().getName();
            // If we are on default branch, plugin is disabled (no diff)
            if(currentBranchName.equals(getDefaultBranchName(currentRepo))){
                System.out.println("BranchFiles: on default branch, no diff");
                event.getPresentation().setEnabled(false);
            } else {
                event.getPresentation().setEnabled(true);
            }
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
        } else return;

        try {
            // Get Latest Git diff
            final Collection<String> latestDiff = GitUtil.getPathsDiffBetweenRefs(
                    Git.getInstance(),
                    currentRepo,
                    currentBranchName,
                    getDefaultBranchName(currentRepo)
            );

            List<VirtualFile> latestDiffList = new ArrayList<>();
            Set<String> latestDiffSet = new HashSet<>();
            VirtualFile tempFile;

            for(String pathDiff: latestDiff) {
                if(!latestDiffSet.contains(pathDiff)){
                    System.out.println("BranchFiles: path diff " + pathDiff);
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
            closeCurrentlyOpenFiles(currentProject);

            // Open Files
            System.out.println("BranchFiles: Opening Recent Branch Files...");
            for(VirtualFile file: latestDiffList){
                new OpenFileDescriptor(currentProject, file).navigate(true);
            }
        } catch (VcsException e) {
            System.out.println("BranchFiles: error while trying to open files" + e);
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

    private static void closeCurrentlyOpenFiles(Project currentProject) {
        System.out.println("BranchFiles: Closing currently open files...");
        FileEditorManager manager = FileEditorManager.getInstance(currentProject);
        VirtualFile currentOpenFiles[] = manager.getOpenFiles();
        for(VirtualFile currentOpenFile: currentOpenFiles){
            manager.closeFile(currentOpenFile);
        }
    }

    private String getDefaultBranchName(GitRepository currentRepo) {
        String defaultBranchToUse;
        // check for "main" to exist if not use "master"
        GitBranchesCollection localBranches = currentRepo.getBranches();
        if(localBranches.findLocalBranch("main") != null){
            defaultBranchToUse = "main";
        } else {
            defaultBranchToUse = "master";
        }
        System.out.println(String.format("BranchFiles: using branch %s as default", defaultBranchToUse));

        // TODO Can I find default branch name from git manager?
        // TODO maybe user input of which branch to diff on? then fallback on main/master
        return defaultBranchToUse;
    }
}