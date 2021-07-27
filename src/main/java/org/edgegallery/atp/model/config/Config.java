package org.edgegallery.atp.model.config;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Config extends ConfigBase {
    /**
     * config id.
     */
    String id;

    /**
     * config createTime.
     */
    Date createTime;

    /**
     * set create time.
     *
     * @param createTime createTime
     */
    public void setCreateTime(Date createTime) {
        if (createTime != null) {
            this.createTime = (Date) createTime.clone();
        } else {
            this.createTime = null;
        }
    }

    /**
     * get create time.
     *
     * @return date
     */
    public Date getCreateTime() {
        return createTime != null ? (Date) createTime.clone() : null;
    }

}
