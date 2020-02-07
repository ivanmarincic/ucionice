package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.ClassroomFeatureType
import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.types.EnumIntegerType
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "classroom_features")
data class ClassroomFeature(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(foreign = true)
    var classroom: Classroom = Classroom(),
    @DatabaseField
    var feature: String = "",
    @DatabaseField(persisterClass = EnumIntegerType::class)
    var type: ClassroomFeatureType = ClassroomFeatureType.NONE
)
