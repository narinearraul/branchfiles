# BranchFiles 

![BranchFilesLogo](https://user-images.githubusercontent.com/2958046/223354456-a04c16e6-9dc0-4056-9c89-194c73d82b00.jpg)

## Description

This plugin will close the files currently open in the IDE and open 6 (not customizable yet for Beta) 
of the last modified files in the current branch. Note currently BranchFiles only supports current branches
that are different from the default branch of the git repo since there is no diff. 

Available in the Tools menu: Tools -> Open Branch Files

![branchFiles](https://user-images.githubusercontent.com/2958046/222947484-02650c27-21a4-476f-9799-d6e7a39cf9ce.png)


## Getting Started

### Installation steps:
- Install a compatible JetBrains IDE, such as IntelliJ IDEA, CLion, PyCharm, or other IntelliJ-based IDEs
- Launch the IDE and open plugin settings
- Search for Branch Files and click install
- After completing installation, make sure the "Open Branch Files" prompt shows up under the Tools Tab (Note: this might be disabled/enabled depending on the current project open in the IDE.
- Open a project that is a git repository
- If on a default (main/master) branch, change to a different branch that has diff-s from the default branch. This is how the plugin determines which files to show.


## Prerequisites 
- Project needs to be a git repository
- Current branch needs to be different from the deafult branch (plugin diffs based on default branch)
