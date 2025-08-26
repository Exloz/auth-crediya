package co.com.bancolombia.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Role {
  private String roleId;
  private String roleName;
  private String description;
}
