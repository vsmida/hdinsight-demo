package clustering

import com.twitter.scalding.{Tsv, Job, Args}

class SimpleFilter(args: Args) extends Job(args) {
  val input = Tsv(args("input"), ('city, 'review_count, 'name, 'neighborhoods, 'type, 'business_id, 'full_address, 'state, 'hours, 'longitude, 'stars, 'latitude, 'attributes, 'open, 'categories))
  val output = Tsv(args("output"))

  val pipe = input
    .read
    .project('latitude, 'longitude)
    .write(output)
}
