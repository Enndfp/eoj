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

![image-20250312150237368](https://img.enndfp.cn/202503121502604.png)

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

EOJ system provides a set of fully functional and interactive user interfaces, which fully reflects the modern design concept and excellent user experience:

- **Question list page** 📋: As shown in Figure 1, a clear table layout is used to display the question list, including key information such as status, question name, label, pass rate and difficulty. The top provides a multi-dimensional filtering function, which supports accurate search by question name, label and difficulty, helping users to quickly locate the required programming challenges. The color logo intuitively displays the question status: a green check mark indicates that it has passed, a yellow error indicates that the attempt has not passed, and a gray bar indicates a question that has not been attempted.

![image-20250312150346416](https://img.enndfp.cn/202503121515059.png)

- **Question details and code editing page** 💻: As shown in Figure 2, the left side shows the detailed description of the question, input and output requirements and examples, and the right side is a professional code editor based on Monaco Editor, which supports syntax highlighting and code completion. The interface provides a language selection drop-down box and theme switching function. The prompt information above the editor guides users to write code as required. The time and memory limits are clearly displayed at the bottom of the page, and "Run" and "Submit" buttons are provided to facilitate users to test and submit code.

![image-20250312150430703](https://img.enndfp.cn/202503121519288.png)

- **Question answer and execution result page** 🧩: As shown in Figure 3, the system provides a double-column layout with a question solution area on the left and a blank editing area on the right. Users can refer to the sample answers for learning and improvement. The sample answers use color syntax highlighting to enhance readability, and contain Chinese comments to help understand the algorithm ideas. The test case area at the bottom can display the code running results in real time, making it easier for users to verify their own solutions.

![image-20250312152427963](https://img.enndfp.cn/202503121524222.png)

- **Submission record page** 📊: As shown in Figure 4, the system displays all submission records of users in a table, including fields such as submitter, title name, programming language, evaluation information, judgment status, and submission time. Use the visual contrast of green success and red failure to clearly display the code evaluation results. Support filtering by title name, programming language, and judgment status, so that users can track their learning progress.

![image-20250312150530964](https://img.enndfp.cn/202503121521857.png)

- **Submission code details pop-up window** 🔍: As shown in Figure 5, click the submission record to view detailed evaluation information, including indicators such as code content, execution status, running time, and memory usage. The code area provides line numbers and complete syntax highlighting, which makes it easy for users to review and analyze their solutions. The status indicator uses green "Accepted" to clearly indicate the passing status.

![image-20250312150512342](https://img.enndfp.cn/202503121522141.png)

- **Topic management page** 📐: As shown in Figure 6, the system presents all topics in a clear table format, including key information such as topic name, topic description, topic label️, topic difficulty, number of submissions, number of passes, pass rate, time limit, memory limit, creator and creation time. The top of the page provides filtering functions for topic name, topic label️, topic difficulty and creator, which allows users to quickly locate the required topic. There is a paging control at the bottom of the page to support administrators to browse a large number of topics. The entire page design is simple and intuitive, which is convenient for administrators to manage topics.

![image-20250312150622096](https://img.enndfp.cn/202503121538676.png)

- **Question Editing Interface** ⚙️: As shown in Figure 7, administrators can create and edit questions through step-by-step forms, including setting basic information, question description, standard answers, question configuration, and test cases. The interface design is concise and clear, and each step has clear instructions and mandatory items to ensure the integrity and standardization of question creation.

![image-20250312150734114](https://img.enndfp.cn/202503121536195.png)

The entire system UI uses blue as the main color, with a white background and bright color logos, which is in line with the design trend of modern Web applications. The interface layout is reasonable and the operation process is clear, providing users with an immersive programming learning and practice environment. The system's responsive design ensures a good user experience on different devices.

------

## 💡 Future plans

- **Extended language support**: Add support for more programming languages, such as Python, JavaScript, etc.
- **Question integration**: Integrate the system with more question platforms to provide a richer question library.
- **Personalization function**: Provide users with richer personalized settings and learning paths.

<div align="center"> <h3>Thank you for your use and we look forward to your feedback and contributions!</h3> </div>