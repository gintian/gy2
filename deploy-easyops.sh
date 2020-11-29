#!/bin/bash
## 调用运维平台进行自动化发布的示例脚本
## 需要安装jq命令，用于在bash中解析JSON

if [[ ! "$1" =~ [[:alnum:]]{32} ]]; then
    echo "参数错误！"
    echo "用法：deploy-easyops.sh <flowId> [versionName]"
    echo "flowId: 必输，且必须是32位的字母加数字组成的字符串"
    echo "versionName：可选，如果为空，easyops平台会根据当前时间生成一个"
    exit 1
fi

debug=false
# 通过curl调用运维平台的REST API接口，其中的flowId是各应用的流水线ID，VERSION_NAME是版本号，请自行替换成自己项目的。
result=$(curl --location --request POST 'http://172.31.63.118/flows/execution' \
    --header 'Content-Type: application/json' \
    --header 'Host: tool.easyops-only.com' \
    --header 'org: 3038' \
    --header 'user: HRS' \
    --silent \
    --data "{\"flowId\": \"$1\",\"flowInputs\": {\"VERSION_NAME\": \"$2\"}}")

[[ "$debug" == "true" ]] && echo $result | jq

taskId=$(echo $result | jq -r .data.taskId)

## 根据项目的启动停止所需要的时间进行调整
# 轮询间隔，10秒
poll_interval=10
# 最大轮训次数
poll_max=60
# 轮询计数器
poll_counter=0

if [[ "$taskId" == "null" ]]; then
    echo -e "调用运维平台流水线失败，平台返回信息：\n$result"
    exit 1
else
    echo -e "调用运维平台流水线成功，流水线任务编号：$taskId"
    echo $taskId
    while [[ $poll_counter -le $poll_max ]]; do
        poll_result=$(curl --location --request GET "http://172.31.63.118/flows/execution/$taskId" \
        --header 'Host: tool.easyops-only.com' \
        --header 'Content-Type: application/json' \
        --header 'org: 3038' \
        --silent \
        --header 'user: HRS')
        [[ "$debug" == "true" ]] && echo $poll_result | jq
        let poll_counter++
        flowStatus=$(echo $poll_result | jq -r .data.totalStatus)
        if [[ "$flowStatus" == "success" ]]; then
            echo "部署流水线已执行成功！"
            break
        elif [[ "$flowStatus" == "failed" ]]; then
            echo "部署流水线任务执行失败！"
            exit 1 # 使整个流水线失败（当从.gitlab-ci.yml中调用此脚本时）
        elif [[ "$flowStatus" == "executing" ]]; then
            echo "部署流水线运行中，请等待……"
            sleep $poll_interval
        else
            echo "无法解析流水线状态，响应报文："
            echo "$poll_result" | jq
            exit 1 
        fi
    done
fi