■実行に関しての注意点

１．このプログラムを実行するためには，サーバ側にスケジュールのデータベースを作成して，システムに登録しておかなければなりません。手順は次のように行います。

　1-1．Access等のデータベースソフトでデータベースを作成
　　　ファイル名：schedule.mdb
　　　テーブル名：schedule
　　　フィールド：date テキスト形式，hour テキスト形式，content 内容

　1-2．作成したデータベースをシステムに登録
　　　Windowsの場合は次のように行います。

　　　コントロールパネル／ODBCデータソース／システムDSN／追加
　　　　／Microsoft Access Driverを選択／完了
　　　　データソース名：scheduleDB，選択ボタンを押してschedule.mdbを探す

２．サーバ側にてスケジュールの読み書きを管理するアプリケーションを起動します。

		java ScheduleServer

３．クライアント側からスケジュールのプログラムを起動します。

		java Schedule


※このプログラムはイントラネット用のスケジュール管理で，ipアドレスとポート番号を
指定してサーバーのソケットを作成します。この場合，ポート番号を指定しますので，サ
ーバのシステムが使用しているポート番号や他のソフトで使用しているポート番号と同じ
にならないように設定しなければなりません。
