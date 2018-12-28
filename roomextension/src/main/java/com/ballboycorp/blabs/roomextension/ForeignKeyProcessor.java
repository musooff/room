package com.ballboycorp.blabs.roomextension;

import androidx.room.ForeignKey;

/**
 * Created by musooff on 28/12/2018.
 */

class ForeignKeyProcessor {

    String getForeignKeyQuery(ForeignKey foreignKey){
        StringBuilder foreignKeyQuery = new StringBuilder()
                .append("FOREIGN KEY");

        foreignKeyQuery.append("(");
        for (String columnname : foreignKey.childColumns()){
            foreignKeyQuery.append("`").append(columnname).append("`")
                    .append(",");
        }
        return "";
    }

}
