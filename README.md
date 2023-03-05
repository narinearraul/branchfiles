# BranchFiles

## Description

This plugin will close the files currently open in the IDE and open 6 (not customizable yet for Beta) 
of the last modified files in the current branch. Note currently BranchFiles only supports current branches
that are different from the default branch of the git repo since there is no diff. 

Available in the Tools menu: Tools -> Open Branch Files

![branchFiles](https://user-images.githubusercontent.com/2958046/222947484-02650c27-21a4-476f-9799-d6e7a39cf9ce.png)



## Prerequisites 
- Project needs to be a git repository
- Current branch needs to be different from the deafult branch (plugin diffs based on default branch)
