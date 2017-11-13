def gitRepo = "git@github.com:Dataman-Cloud/skate.git"
publicRegistryUrl = "demoregistry.dataman-inc.com"
publicImagePrefix = "${publicRegistryUrl}/skate"
publicRegistryUsername = "guangzhou"
publicRegistryPassword = "Gz-regsistry-2017@"
workRootDir = "/home/apps/jenkins-home/workspace/skate"
publicRepositoryId = "releases-public"
publicRepositoryUrl = "http://106.75.3.66:8081/nexus/content/repositories/releases"
workRootDir = "/home/apps/jenkins-home/workspace/skate"

targetdockerfile = "target/docker/"
sourcedockerfile = "src/main/docker"

    node("master") {
        stage("Prepare") {
            sh "echo version is ${VERSION}, IS_PUSH is ${IS_PUSH}"

            git branch: "master", url: "${gitRepo}"
            sh "git pull origin dev"

						sh "echo 'execute replaceVersion'"
            replaceVersion()
        }

        stage("Release-Build") {
            sh "mvn -DskipTests clean package"
        }

				if (params.VERSION != "") {
						error("Only [master] branch can have version. Please check your input!")
				}

        //������push tag ��ȷ�����������в����ɹ�
        //�ƹ���image�ֿ�
        pushImageToPublicRegistry()

        //�ƹ���Maven�ֿ�
        //push3rdJarToPublicMaven()

        stage("Cleanup") {
            //�ָ�����
            sh "echo 'Reset the version to master-SNAPSHOT'"
            sh "git reset --hard"
        }
    }

/** ��3rd��������������Maven�ֿ� **/
def push3rdJarToPublicMaven() {

    if (params.IS_PUSH == "Yes") {
        // �����������������ֿ⣬���ܰ�����Դ��
        stage("Push-public-parent") {
            //����skate-parent
            //����parentû��jar��������mvn����һ��Ҫд-Dfile����������-Dfile�������дһ����ʵ���ϲ����ƴ˰�
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=pom.xml -Dfile=octopus-api/target/octopus-api-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "echo 'push octopus-parent pom to public maven registry finish.'"
        }

        stage("Push-public-base") {
            //���� �������ã�������
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=config-service/pom.xml -Dfile=config-service/target/config-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=discovery-service/pom.xml -Dfile=discovery-service/target/discovery-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=edge-service/pom.xml -Dfile=edge-service/target/edge-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
//            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=hystrix-dashboard/pom.xml -Dfile=hystrix-dashboard/target/hystrix-dashboard-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "echo 'push base service jar to public maven registry finish.'"
        }

        stage("Push-public-biz") {
            //���� ҵ��
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=user-service/pom.xml -Dfile=user-service/target/octopus-api-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=account-service/pom.xml -Dfile=account-service/target/account-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=shopping-cart-service/pom.xml -Dfile=shopping-cart-service/target/shopping-cart-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=catalog-service/pom.xml -Dfile=catalog-service/target/catalog-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=inventory-service/pom.xml -Dfile=inventory-service/target/inventory-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=order-service/pom.xml -Dfile=order-service/target/order-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=online-store-web/pom.xml -Dfile=online-store-web/target/online-store-web-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"

            sh "echo 'push biz jar to public maven registry finish.'"
        }
    }
}

/** ��images�������Ĳֿ�**/
def pushImageToPublicRegistry() {

    if (params.IS_PUSH == "Yes") {

        stage("Push-image-base") {
            sh "docker build -t ${publicImagePrefix}/config-service:${VERSION} config-service"
            sh "docker build -t ${publicImagePrefix}/discovery-service:${VERSION} discovery-service"
            sh "docker build -t ${publicImagePrefix}/edge-service:${VERSION} edge-service"
            sh "docker login -u ${publicRegistryUsername} -p ${publicRegistryPassword} ${publicRegistryUrl}"
            sh "docker push ${publicImagePrefix}/config-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/discovery-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/edge-service:${VERSION}"
        }

        stage("Push-image-biz") {
            sh "docker build -f user-service/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/user-service:${VERSION} user-service"
            sh "docker build -f account-service/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/account-service:${VERSION} account-service"
            sh "docker build -f shopping-cart-service/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/shopping-cart-service:${VERSION} shopping-cart-service"
            sh "docker build -f catalog-service/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/catalog-service:${VERSION} catalog-service"
            sh "docker build -f inventory-service/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/inventory-service:${VERSION} inventory-service"
            sh "docker build -f order-service/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/order-service:${VERSION} order-service"
            sh "docker build -f online-store-web/${sourcedockerfile}/Dockerfile -t ${publicImagePrefix}/online-store-web:${VERSION} online-store-web"

            sh "docker login -u ${publicRegistryUsername} -p ${publicRegistryPassword} ${publicRegistryUrl}"

            sh "docker push ${publicImagePrefix}/user-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/account-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/shopping-cart-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/catalog-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/inventory-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/order-service:${VERSION}"
            sh "docker push ${publicImagePrefix}/online-store-web:${VERSION}"
        }
    }
}

/** �滻�汾��*/
def replaceVersion() {
    sh "echo Replace master-SNAPSHOT to ${VERSION} in pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' config-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' discovery-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' hystrix-dashboard/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' edge-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' user-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' account-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' shopping-cart-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' catalog-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' inventory-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' order-service/pom.xml"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' online-store-web/pom.xml"

    sh "echo Replace master-SNAPSHOT to ${VERSION} in Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' config-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' discovery-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' hystrix-dashboard/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' edge-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' user-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' account-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' shopping-cart-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' catalog-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' inventory-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' order-service/${sourcedockerfile}/Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' online-store-web/${sourcedockerfile}/Dockerfile"
}
