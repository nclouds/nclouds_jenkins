#!/bin/bash
INSTANCE_ID=$(curl http://169.254.169.254/latest/meta-data/instance-id)
AUTO_SCALING_GROUP_NAME=$(aws autoscaling describe-auto-scaling-instances --region us-east-1 --instance-ids=$INSTANCE_ID --output text --query "AutoScalingInstances[0].AutoScalingGroupName")
aws autoscaling set-instance-protection --region us-east-1 --instance-ids $INSTANCE_ID --auto-scaling-group-name $AUTO_SCALING_GROUP_NAME --protected-from-scale-in

jenkins-slave "$@"

COUNT=$(docker ps | grep -c "jenkins")
if [ "$COUNT" -eq "1" ]
then
  aws autoscaling set-instance-protection --region us-east-1 --instance-ids $INSTANCE_ID --auto-scaling-group-name $AUTO_SCALING_GROUP_NAME --no-protected-from-scale-in
fi
