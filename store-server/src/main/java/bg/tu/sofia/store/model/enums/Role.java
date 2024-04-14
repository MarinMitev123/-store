package bg.tu.sofia.store.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role {
    ADMIN,
    PROD_SUPPLIER,
    CUSTOMER,
    ;
}
