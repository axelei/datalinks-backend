package net.krusher.datalinks.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public interface Foundable {

    Foundling toFoundling();

}
