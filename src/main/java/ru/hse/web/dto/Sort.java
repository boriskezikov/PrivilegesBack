package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.swing.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sort {
    @NonNull
    private String sortBy;
    @NonNull
    private SortOrder type;
}