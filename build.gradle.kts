rootProject.name = "fullstack-app"

include("backend")

import org.gradle.api.tasks.Exec

// Run frontend (npm start)
        tasks.register<Exec>("runFrontend") {
            workingDir = file("frontend")
            commandLine = listOf("npm", "start")
            group = "application"
            description = "Starts the React frontend (npm start)"
        }

// Run backend (Spring Boot)
tasks.register<Exec>("runBackend") {
    dependsOn(":backend:bootRun")
    group = "application"
    description = "Starts the Spring Boot backend"
}


// Run both frontend and backend in parallel
tasks.register("runAll") {
    group = "application"
    description = "Starts both backend and frontend"

    dependsOn("runBackend", "runFrontend")
}
