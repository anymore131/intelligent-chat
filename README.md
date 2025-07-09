# intelligent-chat

基于langchain4j的智能聊天系统。

## 项目描述

一套高扩展性、多模态融合的智能聊天系统，分为用户端和管理员端，通过模块化架构与前沿技术整合，实现了企业级智能交互的闭环解决方案。

## 技术栈

SpringBoot3、WebSocket、Mysql、Mybatis-Plus、Redis、Langchain4j、PgVector、MinIO、Nacos

## 配置准备

配置项位于resources下的application.yml下，需要修改的配置有：

1. Mysql配置
2. Redis配置
3. PgVector配置（postgres插件）
4. 联网搜索配置（使用的是tavily）
5. jwt secret（必须是 Base64 编码的密钥（至少 256 位））
6. MinIO配置

nacos作为配置管理默认没开（pom.xml中被注释掉，包括bootstrap），开启需修改resources下的bootstrap.yaml文件配置nacos。可拆分application.yml中的配置移交到nacos中管理。

## langchain4j的使用情况

ai对话流式输出使用的是高级LLM API，但是多模态对话使用的是低级LLM API（官方并没有实现高级LLM API）

可以在管理员端添加不同平台（在config./LLMConfig.java中查看已编写平台或添加langchain4j支持的平台）的LLM大模型，通过反射实现模型的负载均衡，所以对于未添加的平台需修改LLMConfig.java。

集成tavily搜索引擎实现了联网搜索。

使用PgVector构建高性能向量数据库。

预留了json和function call的实现代码，但在项目中未应用，可以据实现代码进行添加。

Agents和MCP的官方文档还不详细，未作任何实现。