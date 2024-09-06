package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "analysis")
public class Analysis {

    @Id
    private String id;
    @Indexed
    private long createdTime;
    @Indexed
    private String projectKey;
    private Project project;

    @Override
    public String toString() {
        return "Analysis{" +
                "id='" + id + '\'' +
                ", createdTime=" + createdTime +
                ", projectKey='" + projectKey + '\'' +
                ", project=" + project +
                '}';
    }
}
