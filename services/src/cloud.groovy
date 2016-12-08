import jenkins.model.*
import java.util.Arrays
import com.cloudbees.jenkins.plugins.amazonecs.ECSCloud
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate.MountPointEntry


cloud_name = System.getenv('CLOUD_NAME') ?: "ECS-SLAVES"
ecs_cluster_arn = System.getenv('ECS_CLUSTER_ARN') ?: "arn:aws:ecs:us-west-2:123456789012:cluster/jenkins-ecs"
aws_region = System.getenv('AWS_REGION') ?: 'us-east-1'
jenkins_url = System.getenv('JENKINS_URL') ?: 'http://'+"curl -s http://169.254.169.254/latest/meta-data/local-ipv4".execute().text+':8080/'
slave_label = System.getenv('SLAVE_LABEL') ?: 'jnlp-slave'
slave_image = System.getenv('SLAVE_IMAGE') ?: '123456789012.dkr.ecr.us-west-2.amazonaws.com/nclouds-jenkins/jenkins:slave'
slave_jenkins_root = System.getenv('SLAVE_JENKINS_ROOT') ?: '/home/jenkins'
slave_cpu = System.getenv('SLAVE_CPU') ?: 0
slave_memory = System.getenv('SLAVE_MEMORY') ?: 3500 

instance = Jenkins.getInstance()

def mounts = Arrays.asList(
  new MountPointEntry(
    name="docker",
    sourcePath="/var/run/docker.sock",
    containerPath="/var/run/docker.sock",
    readOnly=false)
)

def ecsTemplate = new ECSTaskTemplate(
  templateName="jenkins-slave",
  label=slave_label,
  image=slave_image,
  remoteFSRoot=slave_jenkins_root,
  memory=slave_memory,
  cpu=slave_cpu,
  privileged=false,
  logDriverOptions=null,
  environments=null,
  extraHosts=null,
  mountPoints=mounts
)

ecsCloud = new ECSCloud(
  name=cloud_name,
  templates=Arrays.asList(ecsTemplate),
  credentialsId=null,
  cluster=ecs_cluster_arn,
  regionName=aws_region,
  jenkinsUrl=jenkins_url,
  slaveTimoutInSeconds=60
)

def clouds = instance.clouds
clouds.add(ecsCloud)
instance.save()
