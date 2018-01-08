package FormVO

import models.{Resource, ResourceDetail}

case class ResourceForm(resource: Resource, resourceDetailList: Seq[ResourceDetail])
