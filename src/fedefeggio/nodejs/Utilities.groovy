/* groovylint-disable */
package fedefeggio.nodejs;

class Utilities implements Serializable {

    def steps
    Utilities(steps) { this.steps = steps }

    def install(){
        try {
            steps.sh 'npm run git:postdeploy';
        } catch (Exception e1) {
            steps.echo 'npm run git:postdeploy failed, attempting npm run git:postclone...';
            try {
                steps.sh 'npm run git:postclone';
            } catch (Exception e2) {
                steps.echo 'npm run git:postclone failed, running npm install...';
                steps.sh 'npm install';
            }
        }
    }

    // TODO in other file
    def sendTelegramMessage(String telegramToken, String chatId, String message){
        steps.withEnv(['TELEGRAM_TOKEN=' + telegramToken, 'CHAT_ID=' + chatId, 'MESSAGE=' + message]) {
            steps.sh '''
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                -d chat_id=${CHAT_ID} -d text="${MESSAGE}"
            '''
        }
    }

    // TODO in other file
    def sudoSh(String sudoPwd, String command) {
        steps.withEnv(['SUDO_PWD=' + sudoPwd, 'COMMAND=' + command]) {
            steps.sh '''
                echo \"${SUDO_PWD}\" | sudo -S ${COMMAND}
            '''
        }
    }

    def tryRunPm2Process(String repoName, String fullEnvName, String workspaceName) {
        try {
            steps.sh "pm2 restart '${repoName}:${workspaceName}:${fullEnvName}' --update-env";
        } catch (Exception e) {
            steps.echo 'restart failed, attempting to launch it as new...';
            steps.sh "npm run ${fullEnvName}:pm2 --workspace=${workspaceName}";
        }
    }

    def typeormSetup(Boolean isStaging) {
        if(isStaging){
            sh 'npm run typeorm:setup:staging --workspace=backend';
        }
        else {
            sh 'npm run typeorm:setup --workspace=backend';
        }
        sh 'npm run prettier --workspace=backend';
    }

    def typeormSync(Boolean isStaging) {
        echo 'syncing db...';
        if(isStaging){
            sh 'npm run typeorm:sync:staging --workspace=backend';
        }
        else {
            sh 'npm run typeorm:sync --workspace=backend';
        }
    }

    def runPm2Process(repoName, fullEnvName, workspaceName) {
        try {
            // start or restart the process
            tryRunPm2Process(repoName, fullEnvName, workspaceName);
        }
        catch(Exception e1) {
            steps.echo 'stopping the process...';
            try {
                // stop the process
                steps.sh "pm2 stop '${repoName}:${workspaceName}:${fullEnvName}'";
            }
            catch(Exception e2){
                steps.echo 'cannot stop the process';
            }

            // delete the process
            steps.echo 'deleting the process...';
            steps.sh "pm2 del '${repoName}:${workspaceName}:${fullEnvName}'";

            // start or restart the process
            tryRunPm2Process(repoName, fullEnvName, workspaceName);
        }
    }
}
