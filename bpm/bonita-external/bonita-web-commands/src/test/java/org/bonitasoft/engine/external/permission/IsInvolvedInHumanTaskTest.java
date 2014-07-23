/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.external.permission;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.actor.mapping.ActorMappingService;
import org.bonitasoft.engine.actor.mapping.model.SActor;
import org.bonitasoft.engine.command.SCommandExecutionException;
import org.bonitasoft.engine.command.SCommandParameterizationException;
import org.bonitasoft.engine.core.process.instance.api.ActivityInstanceService;
import org.bonitasoft.engine.core.process.instance.model.SHumanTaskInstance;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.session.model.SSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Celine Souchet
 */
@RunWith(MockitoJUnitRunner.class)
public class IsInvolvedInHumanTaskTest {

    @Mock
    TenantServiceAccessor serviceAccessor;

    @Mock
    ActivityInstanceService activityInstanceService;

    @Mock
    SHumanTaskInstance humanTaskInstance;

    @Mock
    ActorMappingService actorMappingService;

    @Spy
    IsInvolvedInHumanTask isInvolvedInHumanTask = new IsInvolvedInHumanTask();

    @Mock
    SSession session;

    @Before
    public void setup() throws Exception {
        when(serviceAccessor.getActivityInstanceService()).thenReturn(activityInstanceService);
        when(activityInstanceService.getHumanTaskInstance(anyLong())).thenReturn(humanTaskInstance);
        when(serviceAccessor.getActorMappingService()).thenReturn(actorMappingService);
        doReturn(session).when(isInvolvedInHumanTask).getCurrentSession();
    }

    /**
     * Test method for
     * {@link org.bonitasoft.engine.external.actor.IsInvolvedInHumanTask#execute(java.util.Map, org.bonitasoft.engine.service.TenantServiceAccessor)} .
     */
    @Test(expected = SCommandParameterizationException.class)
    public final void should_throw_exception_when_Execute_with_wrong_parameter() throws SCommandParameterizationException, SCommandExecutionException {
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put("BAD_PARAMETER", "aa");

        // When
        isInvolvedInHumanTask.execute(parameters, serviceAccessor);
    }

    @Test(expected = SCommandParameterizationException.class)
    public final void should_throw_exception_when_Execute_with_non_existent_user() throws SCommandParameterizationException, SCommandExecutionException {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, -1l);

        // When
        isInvolvedInHumanTask.execute(parameters, serviceAccessor);
    }

    @Test
    public final void should_return_true_when_current_user_wants_to_execute_assigned_task() throws SCommandParameterizationException,
    SCommandExecutionException {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, 10l);
        parameters.put(IsInvolvedInHumanTask.HUMAN_TASK_INSTANCE_ID_KEY, 100l);

        when(session.getUserId()).thenReturn(4l);
        when(humanTaskInstance.getAssigneeId()).thenReturn(4l);

        // When
        assertThat(isInvolvedInHumanTask.execute(parameters, serviceAccessor)).isSameAs(true);
        verify(session, times(1)).getUserId();
        verify(humanTaskInstance, times(1)).getAssigneeId();
    }

    @Test
    public final void should_return_false_when_current_user_wants_to_execute_someone_else_assigned_task() throws SCommandParameterizationException,
    SCommandExecutionException {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, 5l);
        parameters.put(IsInvolvedInHumanTask.HUMAN_TASK_INSTANCE_ID_KEY, 100l);

        when(session.getUserId()).thenReturn(5l);
        when(humanTaskInstance.getAssigneeId()).thenReturn(4l);

        // When
        assertThat(isInvolvedInHumanTask.execute(parameters, serviceAccessor)).isSameAs(false);
        verify(session, times(2)).getUserId();
        verify(humanTaskInstance, times(1)).getAssigneeId();
    }

    @Test
    public final void should_return_true_when_current_user_wants_to_execute_himself_assigned_task_with_do_for() throws Exception {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, 4l);
        parameters.put(IsInvolvedInHumanTask.HUMAN_TASK_INSTANCE_ID_KEY, 100l);

        when(session.getUserId()).thenReturn(4l);
        when(humanTaskInstance.getAssigneeId()).thenReturn(4l);

        // When
        assertThat(isInvolvedInHumanTask.execute(parameters, serviceAccessor)).isSameAs(true);
        verify(session, times(1)).getUserId();
        verify(humanTaskInstance, times(1)).getAssigneeId();
    }

    @Test
    public final void should_return_true_when_current_user_wants_to_execute_someone_else_assigned_task_with_do_for() throws SCommandParameterizationException,
    SCommandExecutionException {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, 5l);
        parameters.put(IsInvolvedInHumanTask.HUMAN_TASK_INSTANCE_ID_KEY, 100l);

        when(session.getUserId()).thenReturn(4l);

        // When
        assertThat(isInvolvedInHumanTask.execute(parameters, serviceAccessor)).isSameAs(true);
        verify(session, times(2)).getUserId();
    }

    @Test
    public final void should_return_true_when_current_user_wants_to_execute_unassigned_task_when_he_is_in_actor_mapping()
            throws Exception {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, 5l);
        parameters.put(IsInvolvedInHumanTask.HUMAN_TASK_INSTANCE_ID_KEY, 100l);

        when(session.getUserId()).thenReturn(5l);
        final long actorId = 888L;
        final long scopeId = 99L;
        when(humanTaskInstance.getActorId()).thenReturn(actorId);
        when(humanTaskInstance.getProcessDefinitionId()).thenReturn(scopeId);

        final SActor sActor = mock(SActor.class);
        when(actorMappingService.getActor(actorId)).thenReturn(sActor);
        when(sActor.getScopeId()).thenReturn(scopeId);

        // When
        assertThat(isInvolvedInHumanTask.execute(parameters, serviceAccessor)).isSameAs(true);
        verify(session, times(2)).getUserId();
        verify(sActor, times(1)).getScopeId();
    }

    @Test
    public final void should_return_false_when_current_user_wants_to_execute_unassigned_task_when_he_is_not_in_actor_mapping()
            throws Exception {
        // Given
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(IsInvolvedInHumanTask.USER_ID_KEY, 5l);
        parameters.put(IsInvolvedInHumanTask.HUMAN_TASK_INSTANCE_ID_KEY, 100l);

        when(session.getUserId()).thenReturn(5l);
        final long actorId = 888L;
        final long scopeId = 99L;
        final long otherScopeId = 100L;
        when(humanTaskInstance.getActorId()).thenReturn(actorId);
        when(humanTaskInstance.getProcessDefinitionId()).thenReturn(scopeId);

        final SActor sActor = mock(SActor.class);
        when(actorMappingService.getActor(actorId)).thenReturn(sActor);
        when(sActor.getScopeId()).thenReturn(otherScopeId);

        // When
        assertThat(isInvolvedInHumanTask.execute(parameters, serviceAccessor)).isSameAs(false);
        verify(session, times(2)).getUserId();
        verify(sActor, times(1)).getScopeId();
    }
}