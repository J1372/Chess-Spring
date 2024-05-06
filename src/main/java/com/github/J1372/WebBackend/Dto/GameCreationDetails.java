package com.github.J1372.WebBackend.Dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GameCreationDetails {
    public String uuid;
    public String creator;
    public Color hostPlaysAs;
    public String gameType;
}
