/*******************************************************************************
 * Copyright (C) 2013-2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bdm.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bonitasoft.engine.bdm.model.BusinessObject;
import com.bonitasoft.engine.bdm.model.BusinessObjectModel;
import com.bonitasoft.engine.bdm.model.UniqueConstraint;
import com.bonitasoft.engine.bdm.model.field.FieldType;
import com.bonitasoft.engine.bdm.model.field.SimpleField;
import com.sun.codemodel.JDefinedClass;

/**
 * @author Romain Bioteau
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerBDMCodeGeneratorTest {

    private static final String EMPLOYEE_QUALIFIED_NAME = "org.bonitasoft.hr.Employee";

    private static final String SERVER_EMPLOYEE_DAO_IMPL_FILE = "org/bonitasoft/hr/server/EmployeeDAOImpl.java";

    private ServerBDMCodeGenerator serverBDMCodeGenerator;

    @Mock
    private BusinessObject bo;

    @Mock
    private JDefinedClass entity;

    private File destDir;

    @Before
    public void setUp() throws Exception {
        serverBDMCodeGenerator = new ServerBDMCodeGenerator();
        final File tmpFolder = File.createTempFile(ServerBDMCodeGeneratorTest.class.getSimpleName(), "", Files.temporaryFolder());
        tmpFolder.delete();
        destDir = Files.newFolder(tmpFolder.getAbsolutePath());
    }

    @After
    public void tearDown() {
        if (destDir != null) {
            Files.delete(destDir);
        }
    }

    @Test
    public void toDaoImplClassnameShouldAddPointServerToLastPackagePart() {
        final BusinessObject myBo = new BusinessObject();
        myBo.setQualifiedName("com.bonitasoft.business.domain.Stool");

        final String daoImplClassname = new ServerBDMCodeGenerator().toDaoImplClassname(myBo);

        assertThat(daoImplClassname).isEqualTo("com.bonitasoft.business.domain.server.StoolDAOImpl");
    }

    @Test
    public void toDaoImplClassnameShouldAddPointServerOnDefaultPackageBO() {
        final BusinessObject myBo = new BusinessObject();
        myBo.setQualifiedName("Zucchini");

        final String daoImplClassname = new ServerBDMCodeGenerator().toDaoImplClassname(myBo);

        assertThat(daoImplClassname).isEqualTo("server.ZucchiniDAOImpl");
    }

    @Test
    public void serverDAOImplShouldGenerateConstructorWithBusinessDataRepository() throws Exception {
        final BusinessObject employeeBO = new BusinessObject();
        employeeBO.setQualifiedName(EMPLOYEE_QUALIFIED_NAME);

        final BusinessObjectModel bom = new BusinessObjectModel();
        bom.addBusinessObject(employeeBO);
        final SimpleField aField = new SimpleField();
        aField.setName("aField");
        aField.setType(FieldType.STRING);
        employeeBO.getFields().add(aField);
        serverBDMCodeGenerator = new ServerBDMCodeGenerator();
        serverBDMCodeGenerator.generateBom(bom, destDir);

        final String serverDaoImplContent = readGeneratedServerDAOImpl();
        assertThat(serverDaoImplContent).contains("public EmployeeDAOImpl(BusinessDataRepository businessDataRepository)",
                "this.businessDataRepository = businessDataRepository;");
    }

    @Test
    public void serverDAOImplShouldGenerateDefaultFindMethod() throws Exception {
        final BusinessObject employeeBO = new BusinessObject();
        employeeBO.setQualifiedName(EMPLOYEE_QUALIFIED_NAME);

        final BusinessObjectModel bom = new BusinessObjectModel();
        bom.addBusinessObject(employeeBO);
        final SimpleField aField = new SimpleField();
        aField.setName("aField");
        aField.setType(FieldType.STRING);
        employeeBO.getFields().add(aField);
        serverBDMCodeGenerator = new ServerBDMCodeGenerator();
        serverBDMCodeGenerator.generateBom(bom, destDir);

        final String serverDaoImplContent = readGeneratedServerDAOImpl();
        assertThat(serverDaoImplContent).contains("public List<Employee> find(int startIndex, int maxResults)",
                "return businessDataRepository.findListByNamedQuery(\"Employee.find\", Employee.class, queryParameters, startIndex, maxResults);",
                "catch (Exception e)");
    }

    @Test
    public void serverDAOImplShouldGenerate_FindByNamedQuery_ForBOReturningQuery() throws Exception {
        final BusinessObject employeeBO = new BusinessObject();
        employeeBO.setQualifiedName(EMPLOYEE_QUALIFIED_NAME);
        final SimpleField nameField = new SimpleField();
        nameField.setName("name");
        nameField.setType(FieldType.STRING);
        employeeBO.getFields().add(nameField);
        final UniqueConstraint uniqueConstraint = new UniqueConstraint();
        uniqueConstraint.setName("uniqueName");
        uniqueConstraint.setFieldNames(Arrays.asList("name"));
        employeeBO.setUniqueConstraints(Arrays.asList(uniqueConstraint));

        final BusinessObjectModel bom = new BusinessObjectModel();
        bom.addBusinessObject(employeeBO);
        serverBDMCodeGenerator = new ServerBDMCodeGenerator();
        serverBDMCodeGenerator.generateBom(bom, destDir);

        final String serverDaoImplContent = readGeneratedServerDAOImpl();
        assertThat(serverDaoImplContent).contains("public Employee findByName(String name)",
                "return businessDataRepository.findByNamedQuery(\"Employee.findByName\", Employee.class, queryParameters);");
    }

    @Test
    public void serverDAOImplShouldGenerate_FindListByNamedQuery_ForListOfBOReturningQuery() throws Exception {
        final BusinessObject employeeBO = new BusinessObject();
        employeeBO.setQualifiedName(EMPLOYEE_QUALIFIED_NAME);
        final SimpleField nameField = new SimpleField();
        nameField.setName("name");
        nameField.setType(FieldType.STRING);
        employeeBO.getFields().add(nameField);

        final BusinessObjectModel bom = new BusinessObjectModel();
        bom.addBusinessObject(employeeBO);
        serverBDMCodeGenerator = new ServerBDMCodeGenerator();
        serverBDMCodeGenerator.generateBom(bom, destDir);

        final String serverDaoImplContent = readGeneratedServerDAOImpl();
        assertThat(serverDaoImplContent).contains("public List<Employee> findByName(String name, int startIndex, int maxResults)",
                "return businessDataRepository.findListByNamedQuery(\"Employee.findByName\", Employee.class, queryParameters, startIndex, maxResults);");
    }

    @Test
    public void serverDAOImplShouldGenerateGoodReturnTypeForCustomQuery() throws Exception {
        final BusinessObject employeeBO = new BusinessObject();
        employeeBO.setQualifiedName(EMPLOYEE_QUALIFIED_NAME);
        final SimpleField nameField = new SimpleField();
        nameField.setName("name");
        nameField.setType(FieldType.STRING);
        employeeBO.getFields().add(nameField);
        employeeBO.addQuery("howManyAreWe", "SELECT count(*) FROM Employee", Long.class.getName());

        final BusinessObjectModel bom = new BusinessObjectModel();
        bom.addBusinessObject(employeeBO);
        serverBDMCodeGenerator = new ServerBDMCodeGenerator();
        serverBDMCodeGenerator.generateBom(bom, destDir);

        final String serverDaoImplContent = readGeneratedServerDAOImpl();
        assertThat(serverDaoImplContent).contains("public Long howManyAreWe()",
                "return businessDataRepository.findByNamedQuery(\"Employee.howManyAreWe\", Long.class, queryParameters);");
    }

    private String readGeneratedServerDAOImpl() throws IOException {
        final File daoImplem = new File(destDir, SERVER_EMPLOYEE_DAO_IMPL_FILE);
        return FileUtils.readFileToString(daoImplem);
    }

}