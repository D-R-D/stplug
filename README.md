# stplug

minecraftサーバーにどうぬうしてコマンドから任意のポートにUDPソケットで定型文を送るやーつ

コード内容はいたって簡単
Bungeecordを子プロセスとして起動して遊ぶプログラムsins("https://github.com/D-R-D/sins")

に"container:start:example"みたいな定型文を送ってるだけで、minecraftのゲーム内からsinsを通してコンテナ起動したい場合は必須のプラグインです。

コマンド

conteiner + (半角スペース) + (start or stop) + (半角スペース) + コンテナ名

導入はプラグインフォルダに入れるだけ！！
minecraftハブサーバー用にどうぞ
