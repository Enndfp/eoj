<img src="https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202502131317965.jpg" alt="eoj" style="zoom:50%;" />

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

![image-20250312150237368](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121502604.png)

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

EOJ系统提供了一套功能完备、交互友好的用户界面，充分体现了现代化的设计理念和优良的用户体验：

- **题目列表页面** 📋：如图1所示，采用清晰的表格布局展示题目列表，包含状态、题目名称、标签、通过率和难度等关键信息。顶部提供多维度筛选功能，支持按题目名称、标签和难度进行精准搜索，帮助用户快速定位需要的编程挑战。色彩标识直观显示题目状态：绿色对勾表示已通过，黄色错误表示尝试未通过，灰色横杠表示尚未尝试的题目。

  ![image-20250312150346416](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121515059.png)

- **题目详情与代码编辑页面** 💻：如图2所示，左侧展示题目详细描述、输入输出要求和示例，右侧是基于Monaco Editor的专业代码编辑器，支持语法高亮和代码补全。界面提供语言选择下拉框和主题切换功能，编辑器上方的提示信息引导用户按照要求编写代码。页面底部清晰显示时间和内存限制，提供"运行"和"提交"按钮方便用户测试和提交代码。

  ![image-20250312150430703](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121519288.png)

- **题目答案与执行结果页面** 🧩：如图3所示，系统提供左侧题解区和右侧空白编辑区的双栏布局，用户可以参考示例答案进行学习和改进。示例答案使用彩色语法高亮增强可读性，并包含中文注释帮助理解算法思路。底部的测试用例区域可实时展示代码运行结果，便于用户验证自己的解决方案。

  ![image-20250312152427963](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121524222.png)

- **提交记录页面** 📊：如图4所示，系统以表格形式展示用户的所有提交记录，包含提交者、题目名称、编程语言、评测信息、判题状态、提交时间等字段。使用绿色成功和红色失败的视觉对比，清晰展示代码评测结果。支持按题目名称、编程语言和判题状态进行筛选，方便用户追踪自己的学习进度。

  ![image-20250312150530964](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121521857.png)

- **提交代码详情弹窗** 🔍：如图5所示，点击提交记录可查看详细的评测信息，包括代码内容、执行状态、运行时间和内存占用等指标。代码区域提供行号和完整的语法高亮，方便用户回顾和分析自己的解决方案。状态标识使用绿色"Accepted"清晰表示通过状态。

  ![image-20250312150512342](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121522141.png)

- **题目管理页面** 📐：如图6所示，系统以清晰的表格形式呈现了所有题目，包括题目名称、题目描述、题目标签️、题目难度、提交数、通过数、通过率、时间限制、内存限制、创建者和创建时间等关键信息。页面顶部提供了题目名称、题目标签️、题目难度和创建者的筛选功能，方便用户快速定位所需题目。页面底部则设有分页控件，支持管理员浏览大量题目。整个页面设计简洁直观，便于管理员对题目进行管理。

  ![image-20250312150622096](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121538676.png)

- **题目编辑界面** ⚙️：如图7所示，管理员可以通过分步骤表单创建和编辑题目，包括设置基本信息、题目描述、标准答案、判题配置和测试用例等内容。界面设计简洁明了，每个步骤都有明确的指引和必填项标识，确保题目创建的完整性和规范性。

  ![image-20250312150734114](https://enndfp-image.oss-cn-guangzhou.aliyuncs.com/202503121536195.png)

整个系统UI采用蓝色为主色调，搭配白色背景和明亮的色彩标识，符合现代Web应用的设计趋势。界面布局合理，操作流程清晰，为用户提供了沉浸式的编程学习和实践环境。系统的响应式设计确保在不同设备上都能获得良好的使用体验。

------

## 💡 未来计划

- **扩展语言支持**：增加更多编程语言的支持，如 Python、JavaScript 等。
- **题目集成**：将系统与更多的题目平台进行集成，提供更丰富的题目库。
- **个性化功能**：为用户提供更丰富的个性化设置与学习路径。

<div align="center"> <h3>感谢您的使用，期待您的反馈与贡献！</h3> </div>
