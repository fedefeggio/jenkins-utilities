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
            steps.sh 'npm run typeorm:setup:staging --workspace=backend';
        }
        else {
            steps.sh 'npm run typeorm:setup --workspace=backend';
        }
        steps.sh 'npm run prettier --workspace=backend';
    }

    def typeormSync(Boolean isStaging) {
        if(isStaging){
            steps.sh 'npm run typeorm:sync:staging --workspace=backend';
        }
        else {
            steps.sh 'npm run typeorm:sync --workspace=backend';
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
