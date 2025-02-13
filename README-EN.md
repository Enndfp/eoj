<img src="https://img.enndfp.cn/202502131317965.jpg" alt="eoj" style="zoom:50%;" />

[简体中文](README.md) | **English**



<div align="center">
<h1>🌟 EOJ - Online Examination System 🚀</h1>
</div> 

<div align="center">
<b>🛠️ Online programming question assessment and management system</b>
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



## 📖 Project Introduction

**EOJ - Online Judging System** is a distributed online programming question evaluation platform built on **Spring Boot + Spring Cloud + Docker**, which aims to provide developers and students with an efficient platform for programming question management, online writing, submission and automatic evaluation.

### Project Features

- **Question Management and Search**: Administrators can create and manage questions, and users can search for questions based on tags, difficulty and other conditions.

- **Code Writing and Submission**: Users can write code online and select a language to submit.

- **Automatic Evaluation and Judging**: The system automatically evaluates the code submitted by the user to determine whether the result is correct.

- **Code Sandbox**: A custom code sandbox service to ensure that the code is executed in a safe environment.

- **Microservice Architecture**: Based on the design of microservices, it is built with Spring Cloud Alibaba to ensure high scalability and high maintainability of the system.

- **Docker Isolation**: Use Docker containers to isolate and execute code to improve security and stability.

## 🔍 Problems solved

- **Improve development efficiency** ⏱️: Automate the evaluation of repetitive programming questions and reduce manual intervention.
- **Code security and isolation** 🔒: Use Docker to implement code sandbox to ensure that the code submitted by users is executed in an isolated environment to improve system security.
- **Multi-language support** 🌐: Support question evaluation and feedback in multiple programming languages.
- **Convenient question management and evaluation result display** 📊: Provide an intuitive front-end interface and back-end management platform for users and administrators to operate.

## 📐 Project design

### 🔧 Core principle

The core function of the system is to compare the code submitted by the user with the test case of the question to determine whether the code output meets the requirements. To achieve this goal, the system is divided into three main modules:

1. **Front-end page**: Based on Vue 3 and Arco Design component library, it realizes the functions of question browsing, code writing, submission and result viewing.
2. **Backend service**: Based on Spring Boot and Spring Cloud, it handles the management of questions, user requests, and code evaluation logic.
3. **Code sandbox**: An independent service responsible for compiling and executing the code submitted by the user in a safe environment and returning the execution results.

------

### 🖥️ Core business process diagram

This diagram shows the key steps of user operation, background processing and data flow in the system. It includes the interaction process of user submitting code on the front-end page, back-end task processing and question judging service, and elaborates on the whole process from task submission to the final result returned to the user.

![image-20250212221214081](https://img.enndfp.cn/202502122212351.png)

------

### 🛠️ Key technologies and implementation

#### 1. **Front-end implementation**

- Use **Vue 3** and **Arco Design** component libraries to build the front-end, providing a responsive interface and convenient interactive experience.

- Use **Vuex** for global state management to ensure the storage and management of front-end user information.

- Use **Monaco Editor** to implement code editing functions and support multi-language highlighting.

#### 2. **Back-end implementation**

- Based on **Spring Boot** and **Spring Cloud** microservice architecture design, it is divided into multiple services: user service, question service, judgment service, etc.

- **Nacos** is used as the service registration and configuration center, and **OpenFeign** implements calls between microservices.

- Use **Redis** to store user session information and implement distributed Session management.

#### 3. **Code sandbox service**

- Use **Docker** to implement isolated code operation to prevent users from submitting malicious code and affecting the system.
- Use **Java Runtime.exec()** method to run Java program, limit memory and execution time, and ensure the security and stability of code.

#### 4. **Judgment mechanism**

- Use **strategy mode** to handle the judgment logic of different programming languages to ensure scalability.
- The judgment service is processed asynchronously through **RabbitMQ** to improve system response speed and throughput.

------

### 🌐 System interface and display

#### Question management and search interface

In the administrator page, administrators can easily create, edit and delete questions. At the same time, users can search and view detailed information of different questions through the front-end page.

![image-20250213122756990](https://img.enndfp.cn/202502131227270.png)

#### User code submission and evaluation result display

Users can write code directly on the page and view the evaluation results after submission. The system will give compilation, execution output and judgment feedback.

![image-20250213123308841](https://img.enndfp.cn/202502131233230.png)

![image-20250213123325755](https://img.enndfp.cn/202502131233110.png)

------

## 💡 Future plans

- **Extended language support**: Add support for more programming languages, such as Python, JavaScript, etc.
- **Question integration**: Integrate the system with more question platforms to provide a richer question library.
- **Personalization function**: Provide users with richer personalized settings and learning paths.

<div align="center"> <h3>Thank you for your use and we look forward to your feedback and contributions!</h3> </div>