package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.Employee;
import com.onedayoffer.taskdistribution.repositories.entities.Task;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) {
        List<Employee> employees;
        if (sortDirection != null && !sortDirection.isEmpty()) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            employees = employeeRepository.findAllAndSort(Sort.by(direction, "fio"));
        } else {
            employees = employeeRepository.findAll();
        }
        Type listType = new TypeToken<List<EmployeeDTO>>() {}.getType();
        return modelMapper.map(employees, listType);
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Сотрудник не найден"));
        Type listType = new TypeToken<EmployeeDTO>() {}.getType();
        return modelMapper.map(employee, listType);
    }

    public List<TaskDTO> getTasksByEmployeeId(Integer id) {
        List<Task> tasks = taskRepository.findAllByEmployeeId(id);
        Type listType = new TypeToken<List<TaskDTO>>() {}.getType();
        return modelMapper.map(tasks, listType);
    }

    @Transactional
    public void changeTaskStatus(Integer taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Задача не найдена"));
        task.setStatus(status);
        taskRepository.saveAndFlush(task);
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) {
        Task task = new Task();
        task.setEmployee(new Employee(employeeId));
        task.setName(newTask.getName());
        task.setTaskType(newTask.getTaskType());
        task.setPriority(newTask.getPriority());
        task.setLeadTime(newTask.getLeadTime());
        task.setStatus(newTask.getStatus());
        taskRepository.saveAndFlush(task);
    }
}
