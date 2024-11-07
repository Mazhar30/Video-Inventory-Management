import React, { useEffect, useState } from 'react';
import ReactPaginate from 'react-paginate';
import { fetchVideos, deleteVideo, updateVideo } from '../../api/api';
import '../../static/css/VideoManagement.css';

const VideoManagement = () => {
    const [videos, setVideos] = useState([]);
    const [editingVideo, setEditingVideo] = useState(null);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedDescription, setEditedDescription] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const itemsPerPage = 7;

    useEffect(() => {
        const loadVideos = async () => {
            try {
                const fetchedVideos = await fetchVideos();
                setVideos(fetchedVideos);
            } catch (error) {
                console.error('Error loading videos:', error);
            }
        };
        loadVideos();
    }, []);

    const handleDelete = async (videoId) => {
        try {
            await deleteVideo(videoId);
            setVideos(videos.filter(video => video.id !== videoId));
        } catch (error) {
            console.error('Error deleting video:', error);
        }
    };

    const handleEdit = (video) => {
        setEditingVideo(video);
        setEditedTitle(video.title);
        setEditedDescription(video.description);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        const updatedVideo = {
            ...editingVideo,
            title: editedTitle,
            description: editedDescription,
        };

        try {
            await updateVideo(updatedVideo);
            setVideos(videos.map(video => (video.id === updatedVideo.id ? updatedVideo : video)));
            setEditingVideo(null);
        } catch (error) {
            console.error('Error updating video:', error);
        }
    };

    const handlePageClick = (data) => {
        setCurrentPage(data.selected);
    };

    const offset = currentPage * itemsPerPage;
    const currentVideos = videos.slice(offset, offset + itemsPerPage);
    const pageCount = Math.ceil(videos.length / itemsPerPage);

    return (
        <div className="video-management-container">
            <h2>Video Management</h2>
            {videos.length === 0 ? (
                <p>No video uploaded</p>
            ) : editingVideo ? (
                <div className="edit-form">
                    <h4>Edit Video</h4>
                    <form onSubmit={handleUpdate}>
                        <input
                            type="text"
                            value={editedTitle}
                            onChange={(e) => setEditedTitle(e.target.value)}
                            required
                            placeholder="Video Title"
                        />
                        <textarea
                            value={editedDescription}
                            onChange={(e) => setEditedDescription(e.target.value)}
                            required
                            placeholder="Video Description"
                        />
                        <button type="submit">Update</button>
                        <button type="button" onClick={() => setEditingVideo(null)}>Cancel</button>
                    </form>
                </div>
            ) : (
                <div>
                    <table className="video-management-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentVideos.map(video => (
                                <tr key={video.id}>
                                    <td>{video.title}</td>
                                    <td>{video.description}</td>
                                    <td>
                                        <button onClick={() => handleEdit(video)}>Edit</button>
                                        <button onClick={() => handleDelete(video.id)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    <ReactPaginate
                        previousLabel={"← Previous"}
                        nextLabel={"Next →"}
                        breakLabel={"..."}
                        pageCount={pageCount}
                        marginPagesDisplayed={2}
                        pageRangeDisplayed={3}
                        onPageChange={handlePageClick}
                        containerClassName={"pagination"}
                        activeClassName={"active"}
                    />
                </div>
            )}
        </div>
    );
};

export default VideoManagement;