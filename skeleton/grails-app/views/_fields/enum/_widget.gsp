<g:if test="${constraints?.inList}">
<g:select name="${property}" from="${constraints.inList}" value="${value}" class="form-control"/>
</g:if>
<g:else>
<g:select name="${property}" from="${type.values()}" value="${value}" class="col-2 form-control"/>
</g:else>
