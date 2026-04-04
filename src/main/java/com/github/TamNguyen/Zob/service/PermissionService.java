package com.github.TamNguyen.Zob.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.domain.Permission;
import com.github.TamNguyen.Zob.domain.response.ResultPaginationDTO;
import com.github.TamNguyen.Zob.repository.PermissionRepository;
import com.github.TamNguyen.Zob.util.error.ConflictException;
import com.github.TamNguyen.Zob.util.error.NotFoundException;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Optional<Permission> fetchById(long id) {
        return this.permissionRepository.findById(id);
    }

    public Permission fetchByIdOrThrow(long id) {
        return this.fetchById(id)
                .orElseThrow(() -> new NotFoundException("Permission với id = " + id + " không tồn tại."));
    }

    public Permission create(Permission p) {
        if (this.isPermissionExist(p)) {
            throw new ConflictException("Permission đã tồn tại.");
        }
        return this.permissionRepository.save(p);
    }

    public Permission update(Permission p) {
        Permission permissionDB = this.fetchByIdOrThrow(p.getId());

        if (this.isPermissionExist(p) && !this.isSameName(p)) {
            throw new ConflictException("Permission đã tồn tại.");
        }

        permissionDB.setName(p.getName());
        permissionDB.setApiPath(p.getApiPath());
        permissionDB.setMethod(p.getMethod());
        permissionDB.setModule(p.getModule());

        return this.permissionRepository.save(permissionDB);
    }

    public void delete(long id) {
        // delete permission_role
        Permission currentPermission = this.fetchByIdOrThrow(id);
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pPermissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pPermissions.getTotalPages());
        mt.setTotal(pPermissions.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pPermissions.getContent());
        return rs;
    }

    public boolean isSameName(Permission p) {
        return this.fetchById(p.getId())
                .map(permissionDB -> permissionDB.getName().equals(p.getName()))
                .orElse(false);
    }
}
