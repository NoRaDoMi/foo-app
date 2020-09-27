package vn.zalopay.phucvt.fooapp.model;

import lombok.*;
import vn.zalopay.phucvt.fooapp.common.mapper.DBTable;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage {
  @DBTable(columnName = "id")
  private String id;

  private String type;

  @DBTable(columnName = "sender")
  private String senderId;

  @DBTable(columnName = "receiver")
  private String receiverId;

  @DBTable(columnName = "message")
  private String message;

  @DBTable(columnName = "create_time")
  private long createTime;
}