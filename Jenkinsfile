pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean install' // sh for linux and ios
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Code Coverage') {
            steps {
                bat 'mvn jacoco:report'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }

        // follow the lecture demo for hub.docker.com deployment stages
    }
}
