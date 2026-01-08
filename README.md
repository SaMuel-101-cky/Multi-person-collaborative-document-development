graph LR
    %% 定义执行者样式
    User((注册用户))

    subgraph System [在线文档协作系统]
        UC1(登录/登出)
        UC2(修改个人头像/昵称)
        UC3(创建文档/文件夹)
        UC4(浏览文件列表)
        UC5(重命名文档)
        UC6(删除文档)
        UC7(编辑文档)
        UC8(多人实时协作)
        UC9(上传图片)
        UC10(文档自动保存)
        UC11(查看协作者光标)
    end

    %% 定义关系
    User --- UC1
    User --- UC2
    User --- UC3
    User --- UC4
    User --- UC5
    User --- UC6
    User --- UC7

    %% 包含与扩展关系
    UC7 -.->|extend| UC8
    UC7 -.->|include| UC9
    UC7 -.->|include| UC10
    UC8 -.->|include| UC11

    %% 样式美化
    style User fill:#f9f,stroke:#333,stroke-width:2px
    style System fill:#fff,stroke:#333,stroke-dasharray: 5 5
