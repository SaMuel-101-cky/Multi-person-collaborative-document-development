```mermaid
usecaseDiagram
    actor "注册用户 (User)" as User

    package "在线文档协作系统" {
        
        %% 用户模块
        usecase "登录/登出" as UC1
        usecase "修改个人信息 (头像/昵称)" as UC2

        %% 文件管理模块 (Spring Boot)
        usecase "创建文档/文件夹" as UC3
        usecase "浏览文件列表" as UC4
        usecase "重命名文档" as UC5
        usecase "删除文档" as UC6
        
        %% 核心编辑器模块 (React + Node.js + Y.js)
        usecase "编辑文档" as UC7
        usecase "多人实时协作" as UC8
        usecase "上传图片" as UC9
        usecase "文档自动保存" as UC10
        usecase "查看协作者光标" as UC11

    }


    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7

    UC7 <.. UC8 : <<extend>> \n(当多用户进入时)
    UC7 ..> UC9 : <<include>>
    UC7 ..> UC10 : <<include>>
    UC8 ..> UC11 : <<include>>
```
