/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 相対パスからテキストファイルの中身を取得する機能を持つクラスです.
 * 使用する前にルートとなるパスを指定して初期化する必要があります.
 */
public class TextFileReader {

  private CharsetDecoder defaultDecoder = Charset.forName("UTF-8").newDecoder();
  private Path rootPath;

  /**
   * コンストラクタ.
   * 現在の作業ディレクトリがルートとなる状態でインスタンスを生成します.
   */
  public TextFileReader() {
    init();
  }
  
  /**
   * ファイルを読み込む際のルートとなるディレクトリを現在の作業ディレクトリで初期化します.
   */
  public void init() {
    init(FileSystems.getDefault().getPath(""));
  }

  /**
   * ファイルを読み込む際のルートとなるディレクトリを指定して初期化します.
   * @param rootPath ファイルを読み込む際のルートとなるディレクトリ
   */
  public void init(String rootPath) {
    init(Paths.get(rootPath));
  }

  /**
   * ファイルを読み込む際のルートとなるディレクトリを指定して初期化します.
   * @param rootPath ファイルを読み込む際のルートとなるディレクトリ
   */
  public void init(Path rootPath) {
    this.rootPath = rootPath;
  }

  /**
   * 相対パスを指定してテキストファイルの内容を読み込みます.
   * @param relative 相対パス
   * @return 指定されたテキストファイルの中身
   * @throws IOException ファイルの読み込みに失敗した場合
   */
  public String read(String relative) throws IOException {
    return read(relative, defaultDecoder);
  }
  
  /**
   * 指定された相対パスから、実際にファイルを取得する際のパスを取得します。
   * @param relative 相対パス
   * @return　実際にファイルを取得する際のパス
   */
  public Path getAbsolutePath(String relative) {
    return rootPath.resolve(Paths.get(relative));
  }

  /**
   * 相対パスを指定してテキストファイルの内容を読み込みます.
   * @param relative 相対パス
   * @param decoder テキストファイルの文字コード
   * @return 指定されたテキストファイルの中身
   * @throws IOException ファイルの読み込みに失敗した場合
   */
  public String read(String relative, CharsetDecoder decoder) throws IOException {
    return read(rootPath.resolve(Paths.get(relative)), decoder);
  }

  private String read(Path absolute, CharsetDecoder decoder) throws IOException {
    File file = absolute.toFile();
    if (file.exists()) {
      try (FileInputStream stream = new FileInputStream(file); FileChannel channel = stream.getChannel();) {
        ByteBuffer buf = ByteBuffer.allocateDirect((int) channel.size());
        channel.read(buf);
        buf.flip();
        return decoder.decode(buf).toString();
      }
    }
    throw new IOException(String.format("file[%s] is not exist.", absolute.toString()));
  }
}
