package com.example.Quartz.service;


import com.zanbeel.customUtility.model.CustomResponseEntity;
import com.example.Quartz.model.dto.request.AddDummyRequestDto;
import com.example.Quartz.model.entity.Dummy;

import java.util.List;


public interface DummyService {
    CustomResponseEntity<Dummy> getStudentById(Long userId);

    CustomResponseEntity<List<Dummy>> getAllStudents();

    CustomResponseEntity<String> deleteStudentById(Long studentId);

    CustomResponseEntity<Dummy> addStudent(AddDummyRequestDto addDummyRequestDto);

    CustomResponseEntity<Dummy> updateStudent(Long studentId, AddDummyRequestDto addDummyRequestDto);
}
