package clustering

import com.twitter.scalding._

class Main(args: Args) extends Job(args) {
  //params
  val input = Tsv(args("input"), ('city, 'review_count, 'name, 'neighborhoods, 'type, 'business_id, 'full_address, 'state, 'hours, 'longitude, 'stars, 'latitude, 'attributes, 'open, 'categories))
  val output = Tsv(args("output"))

  val K = args("clusters").toInt
  val convergeDist = args("convergeDist").toDouble
  val maxIter = args("maxIter").toInt

  val pipe = input.read

  val result =
    pipe.map(('latitude, 'longitude) ->('latitude, 'longitude)) {
      data: (Double, Double) =>
        val (latitude, longitude) = data

        (latitude, longitude)

//        var kPoints = new HashMap[Int, (Double, Double)]
//        var tempDist = 1.0
//
//        for (i <- 1 to maxIter) {
//          println("Next iteration: " + (i + 1))
//
//          //cluster and and member point
//          var closest = data.map(p => (closestPoint(p, kPoints), (p, 1)))
//
//          //recalculate cluster center
//          var pointStats = closest.reduceByKey { case ((x1, y1), (x2, y2)) => (x1 + x2, y1 + y2) }
//
//          var newPoints = pointStats.map { pair => (pair._1, pair._2._1 / pair._2._2) }.collect()
//
//          //distance between existing and new cluster centers
//          tempDist = 0.0
//          for (pair <- newPoints) {
//            tempDist += kPoints.get(pair._1).get.squaredDist(pair._2)
//          }
//          //reset cluster centers
//          for (newP <- newPoints) {
//            kPoints.put(newP._1, newP._2)
//          }
//
//          println("Next centers: " + kPoints)
//        }
//
//        println("Final centers: " + kPoints)

    }

  result.write(output)

  def distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float = {
    val earthRadius = 6371000; //meters
    val dLat = Math.toRadians(lat2-lat1)
    val dLng = Math.toRadians(lng2-lng1)
    val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLng/2) * Math.sin(dLng/2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    val dist = (earthRadius * c).toFloat

    return dist
  }
}
