/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;

import static yamane.tritedb.TriteDbBase.*;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */
import java.sql.SQLException;

import yamane.utils.TextFileReader;

/**
 * 相対パスからテキストファイルの中身を取得する機能を持つクラスです.
 * 使用する前にルートとなるパスを指定して初期化する必要があります.
 */
public class SqlFileReader {

  private static SqlFileReader INSTANCE = new SqlFileReader();
  private TextFileReader reader = new TextFileReader();

  /**
   * インスタンスを取得します.
   * @return インスタンス
   */
  public static SqlFileReader instance() {
    return INSTANCE;
  }

  /**
   * ファイルを読み込む際のルートとなるディレクトリを現在の作業ディレクトリで初期化します.
   */
  public static void init() {
    SqlFileReader.init(FileSystems.getDefault().getPath(""));
  }

  /**
   * ファイルを読み込む際のルートとなるディレクトリを指定して初期化します.
   * @param rootPath ファイルを読み込む際のルートとなるディレクトリ
   */
  public static void init(String rootPath) {
    SqlFileReader.init(Paths.get(rootPath));
  }

  /**
   * ファイルを読み込む際のルートとなるディレクトリを指定して初期化します.
   * @param rootPath ファイルを読み込む際のルートとなるディレクトリ
   */
  public static void init(Path rootPath) {
    INSTANCE.reader.init(rootPath);
  }

  /**
   * 相対パスを指定してテキストファイルの内容を読み込みます.
   * @param relative 相対パス
   * @return 指定されたテキストファイルの中身
   * @throws SQLException ファイルの読み込みに失敗した場合
   */
  public String read(String relative) throws SQLException {
    try {
      return reader.read(relative);
    } catch (IOException e) {
      throw new SQLException(format("fileread.io", reader.getAbsolutePath(relative)), e);
    }
  }

  /**
   * 相対パスを指定してテキストファイルの内容を読み込みます.
   * @param relative 相対パス
   * @param decoder テキストファイルの文字コード
   * @return 指定されたテキストファイルの中身
   * @throws SQLException ファイルの読み込みに失敗した場合
   */
  public String read(String relative, CharsetDecoder decoder) throws SQLException {
    try {
      return reader.read(relative, decoder);
    } catch (IOException e) {
      throw new SQLException(format("fileread.io", reader.getAbsolutePath(relative)), e);
    }
  }
}
