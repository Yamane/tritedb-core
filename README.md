# TriteDb-Core

[![](https://jitpack.io/v/Yamane/tritedb-core.svg)](https://jitpack.io/#Yamane/tritedb-core)

JDBCによるデータベースアクセス処理を簡略化するための、小規模なユーティリティクラス群です。

なんとなくDBアクセス処理書かなきゃならないときとかに少し便利です。コード量が少ないので、JDBCの勉強にもいいかも。そうでもないかも。  
もとは Jakarta Commons DbUtils が当時ジェネリクスとか可変長引数とか対応していなかったので、それが使いたいがために自前で改造したもです。
アノテーションまわりまですごく似た実装が出てきたときにはだいぶびっくりしましたが、まあ誰でも考えることだったのだなあ。  
とはいえ個人的には今でも使っているものなので、JSE8までの仕様でリファクタリングとかして晒してみたりしたナニカです。

## インストール

依存ライブラリは無いので、 tritedb-core-x.x.x.jar をダウンロードしてクラスパスに追加するだけで使用可能です。  
ビルドツールを利用する場合には以下を記述してください。

### Maven

1.リポジトリにjtipackを追加

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2.dependencyを追加

```xml
<dependency>
    <groupId>com.github.Yamane</groupId>
    <artifactId>tritedb-core</artifactId>
    <version>0.9.0</version>
</dependency>
```

### Gradle

1.リポジトリにjtipackを追加

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

2.dependenciesに追加

```gradle
dependencies {
    implementation 'com.github.Yamane:tritedb-core:Tag'
}
```



## ライセンス
MIT License