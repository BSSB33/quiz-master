# Quiz Master Frontend

## Setting up the environment

- Install npm (https://www.npmjs.com/get-npm)
    - if the installation was successful, these commands should return some numbers :) 
    
        ```npm --v```
- Install Angular CLI globally using npm

    ```npm install -g @angular/cli```
    - Checking whether Angular CLI has been installed successfully: 
        
        ```ng v```

## Updating the project

Whenever you update your project, always run the follow commands from the root of the project: 

```npm i```

## Start the web-application locally

Please keep in mind, that this technique should never be used in production! 

To host the application for development purposes, run the following: 

```ng serve```

Navigate your browser to *localhost:4200* and you will see the application. 

## Basic architecture of the application

The main source of the app takes place in the *src/app/* folder. 

### What's inside? 

| Path | Description |
| --- | --- |
| *services/* | Communications with the server |
| *inputs/* | Quiz Master own inputs |
| *modules/* | Lazy modules and modules |
| *components/* | Other reusable components |
