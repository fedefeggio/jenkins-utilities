/* groovylint-disable */
package fedefeggio

class Utilities implements Serializable {

    def steps
    Utilities(steps) { this.steps = steps }

    def sudoSh(String sudoPwd, String command) {
        withEnv(['SUDO_PWD=' + sudoPwd, 'COMMAND=' + command]) {
            steps.sh '''
                echo \"${SUDO_PWD}\" | sudo -S ${COMMAND}
            '''
        }
    }

    def tryRunPm2Process(repoName, fullEnvName, workspaceName) {
        try {
            sh "pm2 restart '${repoName}:${workspaceName}:${fullEnvName}' --update-env";
        } catch (Exception e) {
            echo 'restart failed, attempting to launch it as new...';
            sh "npm run ${fullEnvName}:pm2 --workspace=${workspaceName}";
        }
    }

    def runPm2Process(repoName, fullEnvName, workspaceName) {
        try {
            // start or restart the process
            tryRunPm2Process(repoName, fullEnvName, workspaceName);
        }
        catch(Exception e1) {
            echo 'stopping the process...';
            try {
                // stop the process
                sh "pm2 stop '${repoName}:${workspaceName}:${fullEnvName}'";
            }
            catch(Exception e2){
                echo 'cannot stop the process';
            }

            // delete the process
            echo 'deleting the process...';
            sh "pm2 del '${repoName}:${workspaceName}:${fullEnvName}'";

            // start or restart the process
            tryRunPm2Process(repoName, fullEnvName, workspaceName);
        }
    }
}
