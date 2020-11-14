package com.tincery.gaea.api.src;

import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.api.src.extension.AlarmExtension;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class AlarmTupleData extends AbstractSrcData {

    private Location serverLocation;
    private Location clientLocation;
    private Location serverLocationOuter;
    private Location clientLocationOuter;

    @Transient
    private List<AlarmExtension> alarmExtension;

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.alarmExtension);
    }

}
