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

        stage('Docker Build') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    bat 'docker build -t %DOCKERHUB_USERNAME%/temperature-converter:%BUILD_NUMBER% .'
                    bat 'docker tag %DOCKERHUB_USERNAME%/temperature-converter:%BUILD_NUMBER% %DOCKERHUB_USERNAME%/temperature-converter:latest'
                }
            }
        }

        stage('Docker Hub Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    bat 'echo %DOCKERHUB_PASSWORD% | docker login --username %DOCKERHUB_USERNAME% --password-stdin'
                    bat 'docker push %DOCKERHUB_USERNAME%/temperature-converter:%BUILD_NUMBER%'
                    bat 'docker push %DOCKERHUB_USERNAME%/temperature-converter:latest'
                }
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
        }
        cleanup {
            bat 'docker logout || exit 0'
        }
    }
}
