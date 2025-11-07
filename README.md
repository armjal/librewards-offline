# Librewards - Rewarding students for time spent at the library

## A refactoring project
This project initially started out during my studies at university (2020). The aim was to incentivise studying and spending time at the library by rewarding students with points given a certain amount of time spent at the library. This version of the application is actually the predecessor to an improved, "online" version which has the exact same premise, but uses live services to authenticate staff and students, store relevant user and product data, and track user location for a reliable experience. It also uses a QR code scanning system for timer interaction rather than manual codes. The online version should be in its own repo at some point - also as a refactoring project.

This repository and the code within sees to:
- Refactor the code that was developed during university
- Become familiar with Java once again
- Become familiar with Android development

## Commits
The commits are potentially interesting to look through, especially the [initial one](https://github.com/armjal/librewards-offline/commit/b9d95c91468f0628d3afef135ea64eb54cfdc5c0) where the original codebase was committed. This commit compared to more recent ones will show the differences.
Throughout the history, conventional commits were used as an aim to provide further context to changes and understand quicker what constitutes as: refactor, feature, and improvement, or a task.

## User interface snippets
<img width="190" height="390" alt="librewards-timer" src="https://github.com/user-attachments/assets/c04ccf30-67ab-450f-9e4c-67e23700ba77" />
<img width="190" height="390" alt="librewards-welldone" src="https://github.com/user-attachments/assets/ebb8d5fe-0e95-4f8e-8155-f0cb49d6a0e9" />
<img width="190" height="390" alt="librewards-reward" src="https://github.com/user-attachments/assets/3f1f7eca-30c4-42f3-9316-106c0d2914b6" />
<img width="190" height="390" alt="librewards-codeaccepted" src="https://github.com/user-attachments/assets/9f2e5610-7d22-403b-be73-4ff3d97e9991" />

## Noteable amendments
- Modularisation
- Object Oriented Programming
- Instrumented/Unit testing - this was something that a necessity to ensure quality within the app. There were actually a lot of edge cases and issues uncovered through implementing these. Due to ever-changing modules, it was simplest to add these at the end since the application was already in an complete state. Issues that were found can be found in PR's with the prefix `fix:` and this can also be found within the longer list of commits within the individually closed PR's. 
- Dependency injection - passing dependencies around, namely the DBHelper module, was a tedious task and function calls of the helper were scattered everywhere. Testing was also played a huge part in moving forward with a dedicated DI tool. Hilt made passing dependency to modules a streamlined process.
- CI Workflow for running tests on PR/push to main - As I was creating PR's and squash merging, I realised a reliable test runner within Github would give great code confidence prior to doing so. This could be later expanded to include lint checks

## Technologies
- Java (Application code)
- SQLite (DB)
- XML (UI)

## Remaining work
- Unit tests for views (Activity and Fragments)
- Potentially split out functionality within views further into separate modules. This seems to be what the MVVM architecture solves - where it makes sense to split the majority of logic within views into separate modules
- Create repository with Librewards - Online
