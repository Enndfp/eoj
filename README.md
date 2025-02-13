<img src="https://img.enndfp.cn/202502131317965.jpg" alt="eoj" style="zoom:50%;" />

**简体中文** | [English](README-EN.md) 



<div align="center">
<h1>🌟 EOJ - 在线判题系统 🚀</h1>
</div> 

<div align="center">
<b>🛠️ 在线编程题目评测与管理系统</b>
</div> 
<div align="center">
<img src="https://img.shields.io/badge/Java-1.8-orange" />
<img src="https://img.shields.io/badge/SpringBoot-2.6.13-green" />
<img src="https://img.shields.io/badge/SpringCloud-2021.0.8-blue" />
<img src="https://img.shields.io/badge/SpringCloudAlibaba-2021.0.5.0-blue" />
<img src="https://img.shields.io/badge/Mybatis-2.2.2-yellow" />
<img src="https://img.shields.io/badge/MybatisPlus-3.5.2-brightgreen" />
<img src="https://img.shields.io/badge/OpenFeign-3.1.5-blue" />
<img src="https://img.shields.io/badge/JJWT-0.9.1-blueviolet" />
<img src="https://img.shields.io/badge/Redis-compatible-yellowgreen" />
<img src="https://img.shields.io/badge/Redisson-3.21.3-green" />
<img src="https://img.shields.io/badge/RabbitMQ-compatible-yellow" />
<img src="https://img.shields.io/badge/Hutool-5.8.8-brightgreen" />
<img src="https://img.shields.io/badge/CommonsLang3-3.12.0-green" />
<img src="https://img.shields.io/badge/CommonsCollections4-4.4-yellowgreen" />
<img src="https://img.shields.io/badge/Lombok-1.18.30-purple" />
<img src="https://img.shields.io/badge/SpringBootDevTools-2.6.13-orange" />
</div>


## 📖 项目简介

**EOJ - 在线判题系统** 是一个基于 **Spring Boot + Spring Cloud + Docker** 构建的分布式在线编程题目评测平台，旨在为开发者和学生提供一个高效的编程题目管理、在线编写、提交和自动评测的平台。

### 项目特性

- **题目管理与搜索**：管理员可以创建和管理题目，用户可以根据标签、难度等条件进行题目搜索。
- **代码编写与提交**：用户可以在线编写代码，选择语言进行提交。
- **自动评测与判题**：系统自动评测用户提交的代码，判断结果是否正确。
- **代码沙箱**：自定义的代码沙箱服务，确保代码在安全环境中执行。
- **微服务架构**：基于微服务的设计，采用 Spring Cloud Alibaba 构建，确保系统的高可扩展性和高可维护性。
- **Docker隔离**：使用 Docker 容器隔离执行代码，提升安全性和稳定性。

## 🔍 解决的问题

- **提高开发效率** ⏱️：自动化处理重复性编程题目评测，减少人工干预。
- **代码安全与隔离** 🔒：采用 Docker 实现代码沙箱，确保用户提交的代码在隔离环境中执行，提升系统安全性。
- **多语言支持** 🌐：支持多种编程语言的题目评测与反馈。
- **方便的题目管理与评测结果展示** 📊：提供直观的前端界面和后端管理平台，便于用户和管理员进行操作。

## 📐 项目设计

### 🔧 核心原理

系统的核心功能是通过用户提交的代码与题目的测试用例进行比对，判断代码输出是否符合要求。为了实现这一目标，系统分为三个主要模块：

1. **前端页面**：基于 Vue 3 和 Arco Design 组件库，实现题目浏览、代码编写、提交与查看结果的功能。
2. **后端服务**：基于 Spring Boot 和 Spring Cloud，处理题目的管理、用户的请求、以及代码评测逻辑。
3. **代码沙箱**：独立服务，负责在安全的环境中编译、执行用户提交的代码，并返回执行结果。

------

### 🖥️ 核心业务流程图

该图展示了系统中用户操作、后台处理与数据流转的关键步骤。包括用户在前端页面提交代码、后端任务处理与判题服务的交互流程，详细阐述了从任务提交到最终结果返回给用户的全过程。

![image-20250212221214081](https://img.enndfp.cn/202502122212351.png)

------

### 🛠️ 关键技术与实现

#### 1. **前端实现**

- 使用 **Vue 3** 与 **Arco Design** 组件库构建前端，提供响应式界面和便捷的交互体验。
- 使用 **Vuex** 进行全局状态管理，保证前端用户信息的存储与管理。
- 使用 **Monaco Editor** 编辑器实现代码编辑功能，支持多语言高亮。

#### 2. **后端实现**

- 基于 **Spring Boot** 和 **Spring Cloud** 微服务架构设计，拆分为多个服务：用户服务、题目服务、判题服务等。
- **Nacos** 作为服务注册与配置中心，**OpenFeign** 实现微服务之间的调用。
- 使用 **Redis** 存储用户会话信息，实现分布式 Session 管理。

#### 3. **代码沙箱服务**

- 使用 **Docker** 实现代码的隔离运行，避免用户提交恶意代码对系统造成影响。
- 使用 **Java Runtime.exec()** 方法运行 Java 程序，限制内存和执行时间，确保代码的安全与稳定。

#### 4. **判题机制**

- 使用 **策略模式** 处理不同编程语言的判题逻辑，保证可扩展性。
- 判题服务通过 **RabbitMQ** 进行异步处理，提升系统响应速度和吞吐量。

------

### 🌐 系统界面与展示

#### 题目管理与搜索界面

在管理员页面中，管理员可以轻松地创建、编辑和删除题目。同时，用户可以通过前端页面搜索并查看不同题目的详细信息。

![image-20250213122756990](https://img.enndfp.cn/202502131227270.png)

#### 用户代码提交与评测结果展示

用户可以在页面上直接编写代码，提交后可以查看评测结果，系统会给出编译、执行输出与判定反馈。

![image-20250213123308841](https://img.enndfp.cn/202502131233230.png)

![image-20250213123325755](https://img.enndfp.cn/202502131233110.png)

------

## 💡 未来计划

- **扩展语言支持**：增加更多编程语言的支持，如 Python、JavaScript 等。
- **题目集成**：将系统与更多的题目平台进行集成，提供更丰富的题目库。
- **个性化功能**：为用户提供更丰富的个性化设置与学习路径。

<div align="center"> <h3>感谢您的使用，期待您的反馈与贡献！</h3> </div>