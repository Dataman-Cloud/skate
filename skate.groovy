// Author: linzhaoming
// Date: 2017-01-05
// Usage: Jenkins的自动Pipeline构建脚本

//定义常量和变量
def projectName = "skate"
def gitRepo = "git@github.com:Dataman-Cloud/skate.git"
def registryUrl = "192.168.31.34";
def imagePrefix = "${registryUrl}/skate"
def registryUsername = "gzteam"
def registryPassword = "Dataman1234"
def recvEmail = "dxlu@dataman-inc.com"

//定义绑定域变量
publicRegistryUrl = "demoregistry.dataman-inc.com"
publicImagePrefix = "${publicRegistryUrl}/skate"
publicRegistryUsername = "guangzhou"
publicRegistryPassword = "Gz-regsistry-2017@"
publicRepositoryId = "releases-public"
publicRepositoryUrl = "http://106.75.3.66:8081/nexus/content/repositories/releases"
workRootDir = "/home/apps/jenkins-home/workspace/skate"
targetdockerfile = "/target/docker/"
sourcedockerfile = "/src/main/docker/"

node("master") {
    stage("Common") {
        echo "Environment: ${ENV}, Version: ${VERSION}, SubProject: ${SUB_PROJECT}"
//hygieiaDeployPublishStep applicationName: 'skate', artifactDirectory: './', artifactGroup: 'com.dataman.app', artifactName: '*.*', artifactVersion: '${VERSION}', buildStatus: 'InProgress', environmentName: '构建代码'
        //前置检查
        //1. develop分支不需带上版本号, master分支需要带上版本号
        if (params.BRANCH != "origin/master") {
            if (params.VERSION != "") {
                error("Only [master] branch can have version. Please check your input!")
            }
        } else {
            if (params.VERSION == "") {
                error("[master] branch should have version. Please check your input!")
            }

            if (params.SUB_PROJECT != "all") {
                error("[master] Should release all project.")
            }
        }

        //2. 环境检查
        if (params.ENV != "test" && params.ENV != "release") {
            error("[Environment] should be test, release")
        }

        //3. 发布master到生产之前进行二次确认
        if (params.ENV == "release") {
            try {
                timeout(time: 15, unit: 'SECONDS') {
                    input message: '将会直接直接发布Release, 确定要发布吗',
                            parameters: [[$class      : 'BooleanParameterDefinition',
                                          defaultValue: false,
                                          description : '点击将会发布Release',
                                          name        : '发布Release']]
                }
            } catch (err) {
                def user = err.getCauses()[0].getUser()
                error "Aborted by:\n ${user}"
            }
        }
    }
}

//1. 使用构建节点node(xxx")进行构建
if (params.ENV != "release") {
    node("master") {
        stage("Test-Build") {
//	hygieiaDeployPublishStep applicationName: 'skate', artifactDirectory: './', artifactGroup: 'com.dataman.app', artifactName: '*.*', artifactVersion: '${VERSION}', buildStatus: 'InProgress', environmentName: '构建程序包'
            // 1. 从Git中clone代码
            git branch: "dev", url: "${gitRepo}"

						// 2. 运行Maven构建
            if (params.SUB_PROJECT == "all") {
                sh "mvn clean package deploy -Dspring.profiles.active=development"
            } else {
                sh "mvn -f ${SUB_PROJECT} clean package deploy -Dspring.profiles.active=development"
            }
        }

        stage("Sonar") {
            // 3. sonar
            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "hystrix-dashboard") {
                sh "mvn -f hystrix-dashboard sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "config-service") {
                sh "mvn -f config-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "discovery-service") {
                sh "mvn -f discovery-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "edge-service") {
                sh "mvn -f edge-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "user-service") {
                sh "mvn -f user-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "account-service") {
                sh "mvn -f account-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "shopping-cart-service") {
                sh "mvn -f shopping-cart-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "catalog-service") {
                sh "mvn -f catalog-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "inventory-service") {
                sh "mvn -f inventory-service sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "online-store-web") {
                sh "mvn -f online-store-web sonar:sonar"
            }

            if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "order-service") {
                sh "mvn -f order-service sonar:sonar"
            }
        }

				// 4. 构建Image, 并push到Registry中
        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "hystrix-dashboard"){
            stage("img-hystrix") {
                sh "docker build -t ${imagePrefix}/hystrix-dashboard hystrix-dashboard/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/hystrix-dashboard"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "config-service")  {
            stage("img-config") {
                sh "docker build -t ${imagePrefix}/config-service config-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/config-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "discovery-service")  {
            stage("img-discovery") {
                sh "docker build -t ${imagePrefix}/discovery-service discovery-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/discovery-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "edge-service") {
            stage("img-edge") {
                sh "docker build -t ${imagePrefix}/edge-service edge-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/edge-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "user-service") {
            stage("img-user") {
                sh "docker build -t ${imagePrefix}/user-service user-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/user-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "account-service") {
            stage("img-account") {
                sh "docker build -t ${imagePrefix}/account-service account-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/account-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "shopping-cart-service") {
            stage("img-shoopingcart") {
                sh "docker build -t ${imagePrefix}/shopping-cart-service shopping-cart-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/shopping-cart-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "catalog-service") {
            stage("img-catalog") {
                sh "docker build -t ${imagePrefix}/catalog-service catalog-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/catalog-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "inventory-service") {
            stage("img-inventory") {
                sh "docker build -t ${imagePrefix}/inventory-service inventory-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/inventory-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "order-service") {
            stage("img-order") {
                sh "docker build -t ${imagePrefix}/order-service order-service/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/order-service"
            }
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "online-store-web") {
            stage("img-online-store") {
                sh "docker build -t ${imagePrefix}/online-store-web online-store-web/${targetdockerfile}"
                sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
                sh "docker push ${imagePrefix}/online-store-web"
            }
        }
    }
}

//3. 发布到测试(test)环境: Swarm集群
if (params.ENV == "test") {
    node("master") {
        git branch: "dev", url: "${gitRepo}"

        sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "hystrix-dashboard") {
            sh "docker-compose -f docker-compose.yml pull hystrix-dashboard"
            sh "docker-compose -f docker-compose.yml stop hystrix-dashboard"
            sh "docker-compose -f docker-compose.yml rm -f hystrix-dashboard"
            sh "docker-compose -f docker-compose.yml up -d hystrix-dashboard"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "config-service") {
            sh "docker-compose -f docker-compose.yml pull config-service"
            sh "docker-compose -f docker-compose.yml stop config-service"
            sh "docker-compose -f docker-compose.yml rm -f config-service"
            sh "docker-compose -f docker-compose.yml up -d config-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "discovery-service") {
            sh "docker-compose -f docker-compose.yml pull discovery-service"
            sh "docker-compose -f docker-compose.yml stop discovery-service"
            sh "docker-compose -f docker-compose.yml rm -f discovery-service"
            sh "docker-compose -f docker-compose.yml up -d discovery-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "edge-service") {
            sh "docker-compose -f docker-compose.yml pull edge-service"
            sh "docker-compose -f docker-compose.yml stop edge-service"
            sh "docker-compose -f docker-compose.yml rm -f edge-service"
            sh "docker-compose -f docker-compose.yml up -d edge-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "user-service") {
            sh "docker-compose -f docker-compose.yml pull user-service"
            sh "docker-compose -f docker-compose.yml stop user-service"
            sh "docker-compose -f docker-compose.yml rm -f user-service"
            sh "docker-compose -f docker-compose.yml up -d user-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "account-service") {
            sh "docker-compose -f docker-compose.yml pull account-service"
            sh "docker-compose -f docker-compose.yml stop account-service"
            sh "docker-compose -f docker-compose.yml rm -f account-service"
            sh "docker-compose -f docker-compose.yml up -d account-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "shopping-cart-service") {
            sh "docker-compose -f docker-compose.yml pull shopping-cart-service"
            sh "docker-compose -f docker-compose.yml stop shopping-cart-service"
            sh "docker-compose -f docker-compose.yml rm -f shopping-cart-service"
            sh "docker-compose -f docker-compose.yml up -d shopping-cart-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "catalog-service") {
            sh "docker-compose -f docker-compose.yml pull catalog-service"
            sh "docker-compose -f docker-compose.yml stop catalog-service"
            sh "docker-compose -f docker-compose.yml rm -f catalog-service"
            sh "docker-compose -f docker-compose.yml up -d catalog-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "inventory-service") {
            sh "docker-compose -f docker-compose.yml pull inventory-service"
            sh "docker-compose -f docker-compose.yml stop inventory-service"
            sh "docker-compose -f docker-compose.yml rm -f inventory-service"
            sh "docker-compose -f docker-compose.yml up -d inventory-service"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "online-store-web") {
            sh "docker-compose -f docker-compose.yml pull online-store-web"
            sh "docker-compose -f docker-compose.yml stop online-store-web"
            sh "docker-compose -f docker-compose.yml rm -f online-store-web"
            sh "docker-compose -f docker-compose.yml up -d online-store-web"
        }

        if (params.SUB_PROJECT == "all" || params.SUB_PROJECT == "order-service") {
            sh "docker-compose -f docker-compose.yml pull order-service"
            sh "docker-compose -f docker-compose.yml stop order-service"
            sh "docker-compose -f docker-compose.yml rm -f order-service"
            sh "docker-compose -f docker-compose.yml up -d order-service"
        }
    }
}

/** 替换版本号*/
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
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' config-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' discovery-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' hystrix-dashboard/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' edge-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' user-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' account-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' shopping-cart-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' catalog-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' inventory-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' order-service/${sourcedockerfile}Dockerfile"
    sh "sed -i 's|master-SNAPSHOT|${VERSION}|g\' online-store-web/${sourcedockerfile}Dockerfile"

    sh "echo Replace latest to ${VERSION} in docker-compose.yml"
    sh "sed -i 's|:latest|:${VERSION}|g\' docker-compose.yml"
}

//5. 发布到生产(prod)环境: 只有打包master分支, 才进行prod环境部署
if (params.ENV == "release" && params.BRANCH == "origin/master") {
    node("master") {
				sh "echo release for master"
        def tagVersion = "${projectName}-V${VERSION}"

        stage("Prepare") {
            sh "echo Releasing for ${BRANCH}, version is ${VERSION}"
            //1. 从develop分支中获取代码
            git branch: "master", url: "${gitRepo}"
            sh "git pull origin dev"

            //2. 替换版本号
            replaceVersion();
        }

        stage("Release-Build") {
            //3. Maven构建;构建Image, 并push到Registry中
            sh "mvn -DskipTests clean package"
            sh "mvn -DskipTests deploy"
        }

        stage("Img-Conf") {
            sh "docker build -t ${imagePrefix}/config-service:${VERSION} config-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/config-service:${VERSION}"
        }

        stage("Img-Discovery") {
            sh "docker build -t ${imagePrefix}/discovery-service:${VERSION} discovery-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/discovery-service:${VERSION}"
        }

        stage("Img-Edge") {
            sh "docker build -t ${imagePrefix}/edge-service:${VERSION} edge-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/edge-service:${VERSION}"
        }

        stage("Img-Hystrix") {
            sh "docker build -t ${imagePrefix}/hystrix-dashboard:${VERSION} hystrix-dashboard/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/hystrix-dashboard:${VERSION}"
        }

        stage("Img-User") {
            sh "docker build -t ${imagePrefix}/user-service:${VERSION} user-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/user-service:${VERSION}"
        }

        stage("Img-Account") {
            sh "docker build -t ${imagePrefix}/account-service:${VERSION} account-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/account-service:${VERSION}"
        }

        stage("Img-Shopping") {
            sh "docker build -t ${imagePrefix}/shopping-cart-service:${VERSION} shopping-cart-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/shopping-cart-service:${VERSION}"
        }

        stage("Img-Catalog") {
            sh "docker build -t ${imagePrefix}/catalog-service:${VERSION} catalog-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/catalog-service:${VERSION}"
        }

        stage("Img-Inventory") {
            sh "docker build -t ${imagePrefix}/inventory-service:${VERSION} inventory-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/inventory-service:${VERSION}"
        }

        stage("Img-Online") {
            sh "docker build -t ${imagePrefix}/online-store-web:${VERSION} online-store-web/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/online-store-web:${VERSION}"
        }

        stage("Img-Order") {
            sh "docker build -t ${imagePrefix}/order-service:${VERSION} order-service/${targetdockerfile}"
            sh "docker login -u ${registryUsername} -p ${registryPassword} ${registryUrl}"
            sh "docker push ${imagePrefix}/order-service:${VERSION}"
        }

        stage("Cleanup") {
						sh "echo Cleanup"
            //5. 打tag
            sh "git tag ${tagVersion} -m 'Release ${tagVersion}'"
            sh "git push origin master"
            sh "git push origin ${tagVersion}"

            //6. 恢复重置
            sh "echo 'Reset the version to master-SNAPSHOT'"
            sh "git reset --hard"
        }

        //推公网image仓库
        //pushImageToPublicRegistry();

        //推公网Maven仓库
        //push3rdJarToPublicMaven();
    }
}

/** 推3rd依赖包到公网的Maven仓库 **/
def push3rdJarToPublicMaven() {

    if (params.IS_PUSH == "Yes") {
        // 推送依赖包到公网仓库，不能包含有源码
        stage("Push-public-parent") {
            //推送skate-parent
            //由于parent没有jar包，而且mvn命令一定要写-Dfile参数，所以-Dfile这里随便写一个，实际上不会推此包
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=pom.xml -Dfile=octopus-api/target/octopus-api-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "echo 'push octopus-parent pom to public maven registry finish.'"
        }

        stage("Push-public-base") {
            //推送 基础配置，服务发现
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=config-service/pom.xml -Dfile=config-service/target/config-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=discovery-service/pom.xml -Dfile=discovery-service/target/discovery-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=edge-service/pom.xml -Dfile=edge-service/target/edge-service-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "mvn -s ${workRootDir}/settings.xml deploy:deploy-file -DpomFile=hystrix-dashboard/pom.xml -Dfile=hystrix-dashboard/target/hystrix-dashboard-${VERSION}.jar  -DrepositoryId=${publicRepositoryId} -Durl=${publicRepositoryUrl}"
            sh "echo 'push base service jar to public maven registry finish.'"
        }

        stage("Push-public-biz") {
            //推送 业务
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

/** 推images到公网的仓库**/
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
            sh "docker build -f user-service/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/user-service:${VERSION} user-service"
            sh "docker build -f account-service/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/account-service:${VERSION} account-service"
            sh "docker build -f shopping-cart-service/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/shopping-cart-service:${VERSION} shopping-cart-service"
            sh "docker build -f catalog-service/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/catalog-service:${VERSION} catalog-service"
            sh "docker build -f inventory-service/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/inventory-service:${VERSION} inventory-service"
            sh "docker build -f order-service/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/order-service:${VERSION} order-service"
            sh "docker build -f online-store-web/${targetdockerfile}/Dockerfile -t ${publicImagePrefix}/online-store-web:${VERSION} online-store-web"

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